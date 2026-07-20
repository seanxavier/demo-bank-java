package com.scheduler.exception;

/**
 * Exception thrown when a circular dependency is detected in task dependencies.
 * This prevents infinite loops in the scheduling algorithm.
 */
public class CircularDependencyException extends RuntimeException {

    private final Long taskId;
    private final Long dependsOnTaskId;

    public CircularDependencyException(String message) {
        super(message);
        this.taskId = null;
        this.dependsOnTaskId = null;
    }

    public CircularDependencyException(String message, Long taskId, Long dependsOnTaskId) {
        super(message);
        this.taskId = taskId;
        this.dependsOnTaskId = dependsOnTaskId;
    }

    public CircularDependencyException(String message, Throwable cause) {
        super(message, cause);
        this.taskId = null;
        this.dependsOnTaskId = null;
    }

    public Long getTaskId() {
        return taskId;
    }

    public Long getDependsOnTaskId() {
        return dependsOnTaskId;
    }
}

// Made with Bob
