package com.scheduler.aspect;

import com.scheduler.annotation.Loggable;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * AOP Aspect for automatic logging of methods annotated with @Loggable.
 * Logs method entry, exit, parameters, return values, and execution time.
 */
@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Around advice for methods annotated with @Loggable
     */
    @Around("@annotation(loggable)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint, Loggable loggable) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        
        long startTime = System.currentTimeMillis();
        
        // Log method entry
        if (loggable.logParameters()) {
            Object[] args = joinPoint.getArgs();
            logger.debug("Entering {}.{}() with arguments: {}", 
                className, methodName, Arrays.toString(args));
        } else {
            logger.debug("Entering {}.{}()", className, methodName);
        }
        
        Object result = null;
        try {
            // Execute the method
            result = joinPoint.proceed();
            
            // Log method exit with result
            if (loggable.logResult() && result != null) {
                logger.debug("Exiting {}.{}() with result: {}", 
                    className, methodName, result);
            } else {
                logger.debug("Exiting {}.{}()", className, methodName);
            }
            
            return result;
            
        } catch (Exception e) {
            // Log exception
            logger.error("Exception in {}.{}(): {}", 
                className, methodName, e.getMessage(), e);
            throw e;
            
        } finally {
            // Log execution time
            if (loggable.logExecutionTime()) {
                long executionTime = System.currentTimeMillis() - startTime;
                logger.debug("{}.{}() executed in {} ms", 
                    className, methodName, executionTime);
            }
        }
    }

    /**
     * Log all service layer method calls (optional - can be enabled/disabled)
     */
    @Around("execution(* com.scheduler.service..*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        
        logger.info("Service call: {}.{}()", className, methodName);
        
        try {
            Object result = joinPoint.proceed();
            logger.info("Service call completed: {}.{}()", className, methodName);
            return result;
        } catch (Exception e) {
            logger.error("Service call failed: {}.{}() - {}", 
                className, methodName, e.getMessage());
            throw e;
        }
    }

    /**
     * Log all controller method calls
     */
    @Around("execution(* com.scheduler.controller..*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        
        logger.info("API call: {}.{}()", className, methodName);
        
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("API call completed: {}.{}() in {} ms", 
                className, methodName, executionTime);
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("API call failed: {}.{}() after {} ms - {}", 
                className, methodName, executionTime, e.getMessage());
            throw e;
        }
    }
}

// Made with Bob
