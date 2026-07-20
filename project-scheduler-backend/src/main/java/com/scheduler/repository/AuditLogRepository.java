package com.scheduler.repository;

import com.scheduler.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for AuditLog entity.
 * Provides CRUD operations and custom queries for audit trail.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Find audit logs by entity type
     */
    List<AuditLog> findByEntityType(String entityType);

    /**
     * Find audit logs by entity type and ID
     */
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);

    /**
     * Find audit logs by action
     */
    List<AuditLog> findByAction(String action);

    /**
     * Find audit logs by username
     */
    List<AuditLog> findByUsername(String username);

    /**
     * Find audit logs within a time range
     */
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Find audit logs by entity type and action
     */
    List<AuditLog> findByEntityTypeAndAction(String entityType, String action);

    /**
     * Find recent audit logs (ordered by timestamp descending)
     */
    List<AuditLog> findTop100ByOrderByTimestampDesc();

    /**
     * Find audit logs with errors
     */
    List<AuditLog> findByErrorMessageIsNotNull();
}

// Made with Bob
