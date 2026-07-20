package com.scheduler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to mark methods for automatic logging.
 * Methods annotated with @Loggable will have their execution logged
 * including parameters, return values, and execution time.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
    
    /**
     * Optional description of what the method does
     */
    String value() default "";
    
    /**
     * Whether to log method parameters
     */
    boolean logParameters() default true;
    
    /**
     * Whether to log return value
     */
    boolean logResult() default true;
    
    /**
     * Whether to log execution time
     */
    boolean logExecutionTime() default true;
}

// Made with Bob
