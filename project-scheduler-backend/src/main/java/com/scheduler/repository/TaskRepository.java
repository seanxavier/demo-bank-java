package com.scheduler.repository;

import com.scheduler.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Task entity.
 * Provides CRUD operations and custom queries.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find all tasks for a specific project
     */
    List<Task> findByProjectId(Long projectId);

    /**
     * Find tasks by name (case-insensitive)
     */
    List<Task> findByNameContainingIgnoreCase(String name);

    /**
     * Find tasks by status
     */
    List<Task> findByStatus(Task.TaskStatus status);

    /**
     * Find tasks by project and status
     */
    List<Task> findByProjectIdAndStatus(Long projectId, Task.TaskStatus status);

    /**
     * Find task with all dependencies loaded
     */
    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.dependencies WHERE t.id = :id")
    Optional<Task> findByIdWithDependencies(Long id);

    /**
     * Find all tasks with their dependencies for a project
     */
    @Query("SELECT DISTINCT t FROM Task t LEFT JOIN FETCH t.dependencies WHERE t.project.id = :projectId")
    List<Task> findByProjectIdWithDependencies(@Param("projectId") Long projectId);

    /**
     * Find tasks that have no dependencies (can start immediately)
     */
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.dependencies IS EMPTY")
    List<Task> findTasksWithNoDependencies(@Param("projectId") Long projectId);

    /**
     * Count tasks by project
     */
    long countByProjectId(Long projectId);

    /**
     * Check if a task exists by name within a project
     */
    boolean existsByProjectIdAndName(Long projectId, String name);
}

// Made with Bob
