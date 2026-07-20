package com.scheduler.exception;

/**
 * Exception thrown when validation fails for business rules.
 * Used for story points validation, date validation, etc.
 */
public class ValidationException extends RuntimeException {

    private final String field;
    private final Object rejectedValue;

    public ValidationException(String message) {
        super(message);
        this.field = null;
        this.rejectedValue = null;
    }

    public ValidationException(String message, String field, Object rejectedValue) {
        super(message);
        this.field = field;
        this.rejectedValue = rejectedValue;
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.field = null;
        this.rejectedValue = null;
    }

    public String getField() {
        return field;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }
}

// Made with Bob
