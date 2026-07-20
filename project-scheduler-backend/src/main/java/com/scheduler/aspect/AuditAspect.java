package com.scheduler.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scheduler.annotation.Auditable;
import com.scheduler.model.AuditLog;
import com.scheduler.repository.AuditLogRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * AOP Aspect for automatic audit trail tracking.
 * Captures all CRUD operations and stores them in the audit_logs table.
 */
@Aspect
@Component
public class AuditAspect {

    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Around advice for methods annotated with @Auditable
     */
    @Around("@annotation(auditable)")
    public Object auditMethodExecution(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
        
        Object[] args = joinPoint.getArgs();
        String parameters = serializeParameters(args);
        
        Object beforeState = null;
        if (auditable.captureBeforeState() && args.length > 0) {
            beforeState = args[0];
        }
        
        AuditLog auditLog = AuditLog.builder()
                .entityType(auditable.entityType())
                .action(auditable.action())
                .methodName(methodName)
                .parameters(parameters)
                .timestamp(LocalDateTime.now())
                .username("system") // TODO: Get from security context when authentication is added
                .build();
        
        Object result = null;
        try {
            // Execute the method
            result = joinPoint.proceed();
            
            // Capture after state
            if (auditable.captureAfterState() && result != null) {
                String afterState = serializeObject(result);
                auditLog.setResult(afterState);
                
                // If we have before state, capture the changes
                if (beforeState != null) {
                    String changes = captureChanges(beforeState, result);
                    auditLog.setChanges(changes);
                }
            }
            
            // Extract entity ID from result if possible
            if (result != null) {
                Long entityId = extractEntityId(result);
                if (entityId != null) {
                    auditLog.setEntityId(entityId);
                }
            }
            
            // Save audit log
            auditLogRepository.save(auditLog);
            logger.debug("Audit log created for {}.{} - Action: {}", 
                auditable.entityType(), auditable.action(), methodName);
            
            return result;
            
        } catch (Exception e) {
            // Log the error in audit trail
            auditLog.setErrorMessage(e.getMessage());
            auditLogRepository.save(auditLog);
            
            logger.error("Audit log created with error for {}.{} - {}", 
                auditable.entityType(), auditable.action(), e.getMessage());
            throw e;
        }
    }

    /**
     * Serialize method parameters to JSON string
     */
    private String serializeParameters(Object[] args) {
        try {
            if (args == null || args.length == 0) {
                return "[]";
            }
            return objectMapper.writeValueAsString(Arrays.asList(args));
        } catch (Exception e) {
            logger.warn("Failed to serialize parameters: {}", e.getMessage());
            return "Unable to serialize parameters";
        }
    }

    /**
     * Serialize object to JSON string
     */
    private String serializeObject(Object obj) {
        try {
            if (obj == null) {
                return null;
            }
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            logger.warn("Failed to serialize object: {}", e.getMessage());
            return "Unable to serialize object";
        }
    }

    /**
     * Capture changes between before and after states
     */
    private String captureChanges(Object before, Object after) {
        try {
            String beforeJson = serializeObject(before);
            String afterJson = serializeObject(after);
            
            return String.format("Before: %s\nAfter: %s", 
                truncate(beforeJson, 1000), 
                truncate(afterJson, 1000));
        } catch (Exception e) {
            logger.warn("Failed to capture changes: {}", e.getMessage());
            return "Unable to capture changes";
        }
    }

    /**
     * Extract entity ID from result object using reflection
     */
    private Long extractEntityId(Object obj) {
        try {
            // Try to get getId() method
            var method = obj.getClass().getMethod("getId");
            Object id = method.invoke(obj);
            if (id instanceof Long) {
                return (Long) id;
            }
        } catch (Exception e) {
            // Ignore - not all objects have getId()
        }
        return null;
    }

    /**
     * Truncate string to specified length
     */
    private String truncate(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "... (truncated)";
    }
}

// Made with Bob
