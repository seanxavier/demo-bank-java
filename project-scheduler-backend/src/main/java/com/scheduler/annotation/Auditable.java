package com.scheduler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to mark methods for automatic audit trail tracking.
 * Methods annotated with @Auditable will have their execution tracked
 * in the audit_logs table with before/after values and user information.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
    
    /**
     * The type of entity being audited (e.g., "Project", "Task")
     */
    String entityType();
    
    /**
     * The action being performed (e.g., "CREATE", "UPDATE", "DELETE")
     */
    String action();
    
    /**
     * Whether to capture the state before the operation
     */
    boolean captureBeforeState() default true;
    
    /**
     * Whether to capture the state after the operation
     */
    boolean captureAfterState() default true;
}

// Made with Bob
