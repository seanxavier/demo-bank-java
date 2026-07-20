package com.scheduler.repository;

import com.scheduler.model.TaskDependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for TaskDependency entity.
 * Provides CRUD operations and custom queries for dependency management.
 */
@Repository
public interface TaskDependencyRepository extends JpaRepository<TaskDependency, Long> {

    /**
     * Find all dependencies for a specific task
     */
    List<TaskDependency> findByTaskId(Long taskId);

    /**
     * Find all tasks that depend on a specific task
     */
    List<TaskDependency> findByDependsOnTaskId(Long dependsOnTaskId);

    /**
     * Check if a dependency already exists
     */
    boolean existsByTaskIdAndDependsOnTaskId(Long taskId, Long dependsOnTaskId);

    /**
     * Delete all dependencies for a task
     */
    void deleteByTaskId(Long taskId);

    /**
     * Delete all dependencies where a task is the dependency
     */
    void deleteByDependsOnTaskId(Long dependsOnTaskId);

    /**
     * Find all dependencies for tasks in a project
     */
    @Query("SELECT td FROM TaskDependency td WHERE td.task.project.id = :projectId")
    List<TaskDependency> findByProjectId(@Param("projectId") Long projectId);

    /**
     * Count dependencies for a task
     */
    long countByTaskId(Long taskId);

    /**
     * Count tasks that depend on a specific task
     */
    long countByDependsOnTaskId(Long dependsOnTaskId);
}

// Made with Bob
