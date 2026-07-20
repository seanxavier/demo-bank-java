package com.scheduler.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AuditLog entity for tracking all CRUD operations and changes.
 * Used by AOP aspects to maintain an audit trail.
 */
@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String entityType;

    @Column
    private Long entityId;

    @Column(nullable = false)
    private String action;

    @Column
    private String username;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(length = 5000)
    private String changes;

    @Column(length = 2000)
    private String methodName;

    @Column(length = 5000)
    private String parameters;

    @Column(length = 2000)
    private String result;

    @Column(length = 5000)
    private String errorMessage;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if (username == null) {
            username = "system";
        }
    }

    /**
     * Builder pattern for creating audit logs
     */
    public static AuditLogBuilder builder() {
        return new AuditLogBuilder();
    }

    public static class AuditLogBuilder {
        private String entityType;
        private Long entityId;
        private String action;
        private String username;
        private LocalDateTime timestamp;
        private String changes;
        private String methodName;
        private String parameters;
        private String result;
        private String errorMessage;

        public AuditLogBuilder entityType(String entityType) {
            this.entityType = entityType;
            return this;
        }

        public AuditLogBuilder entityId(Long entityId) {
            this.entityId = entityId;
            return this;
        }

        public AuditLogBuilder action(String action) {
            this.action = action;
            return this;
        }

        public AuditLogBuilder username(String username) {
            this.username = username;
            return this;
        }

        public AuditLogBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public AuditLogBuilder changes(String changes) {
            this.changes = changes;
            return this;
        }

        public AuditLogBuilder methodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public AuditLogBuilder parameters(String parameters) {
            this.parameters = parameters;
            return this;
        }

        public AuditLogBuilder result(String result) {
            this.result = result;
            return this;
        }

        public AuditLogBuilder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public AuditLog build() {
            AuditLog auditLog = new AuditLog();
            auditLog.entityType = this.entityType;
            auditLog.entityId = this.entityId;
            auditLog.action = this.action;
            auditLog.username = this.username;
            auditLog.timestamp = this.timestamp != null ? this.timestamp : LocalDateTime.now();
            auditLog.changes = this.changes;
            auditLog.methodName = this.methodName;
            auditLog.parameters = this.parameters;
            auditLog.result = this.result;
            auditLog.errorMessage = this.errorMessage;
            return auditLog;
        }
    }
}

// Made with Bob
