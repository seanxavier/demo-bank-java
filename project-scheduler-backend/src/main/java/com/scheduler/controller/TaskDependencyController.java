package com.scheduler.controller;

import com.scheduler.dto.TaskDependencyDTO;
import com.scheduler.service.TaskDependencyService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Task Dependency management.
 * Provides endpoints for managing task dependencies with circular dependency validation.
 */
@RestController
@RequestMapping("/api/dependencies")
@CrossOrigin(origins = "http://localhost:4200")
public class TaskDependencyController {

    private static final Logger logger = LoggerFactory.getLogger(TaskDependencyController.class);

    @Autowired
    private TaskDependencyService taskDependencyService;

    /**
     * Create a new task dependency
     * POST /api/dependencies
     */
    @PostMapping
    public ResponseEntity<TaskDependencyDTO> createDependency(@Valid @RequestBody TaskDependencyDTO dependencyDTO) {
        logger.info("REST request to create dependency: Task {} depends on Task {}", 
                   dependencyDTO.getTaskId(), dependencyDTO.getDependsOnTaskId());
        TaskDependencyDTO created = taskDependencyService.createDependency(dependencyDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Get dependency by ID
     * GET /api/dependencies/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskDependencyDTO> getDependencyById(@PathVariable Long id) {
        logger.info("REST request to get dependency: {}", id);
        TaskDependencyDTO dependency = taskDependencyService.getDependencyById(id);
        return ResponseEntity.ok(dependency);
    }

    /**
     * Get all dependencies for a task
     * GET /api/dependencies/task/{taskId}
     */
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<TaskDependencyDTO>> getDependenciesForTask(@PathVariable Long taskId) {
        logger.info("REST request to get dependencies for task: {}", taskId);
        List<TaskDependencyDTO> dependencies = taskDependencyService.getDependenciesForTask(taskId);
        return ResponseEntity.ok(dependencies);
    }

    /**
     * Get all tasks that depend on a specific task
     * GET /api/dependencies/depends-on/{taskId}
     */
    @GetMapping("/depends-on/{taskId}")
    public ResponseEntity<List<TaskDependencyDTO>> getTasksThatDependOn(@PathVariable Long taskId) {
        logger.info("REST request to get tasks that depend on: {}", taskId);
        List<TaskDependencyDTO> dependencies = taskDependencyService.getTasksThatDependOn(taskId);
        return ResponseEntity.ok(dependencies);
    }

    /**
     * Get all dependencies for a project
     * GET /api/dependencies/project/{projectId}
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskDependencyDTO>> getDependenciesByProjectId(@PathVariable Long projectId) {
        logger.info("REST request to get dependencies for project: {}", projectId);
        List<TaskDependencyDTO> dependencies = taskDependencyService.getDependenciesByProjectId(projectId);
        return ResponseEntity.ok(dependencies);
    }

    /**
     * Validate if adding a dependency would create a circular dependency
     * POST /api/dependencies/validate
     */
    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateDependency(@Valid @RequestBody TaskDependencyDTO dependencyDTO) {
        logger.info("REST request to validate dependency: Task {} depends on Task {}", 
                   dependencyDTO.getTaskId(), dependencyDTO.getDependsOnTaskId());
        try {
            taskDependencyService.validateNoCircularDependency(
                dependencyDTO.getTaskId(), 
                dependencyDTO.getDependsOnTaskId()
            );
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    /**
     * Delete dependency
     * DELETE /api/dependencies/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDependency(@PathVariable Long id) {
        logger.info("REST request to delete dependency: {}", id);
        taskDependencyService.deleteDependency(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Delete all dependencies for a task
     * DELETE /api/dependencies/task/{taskId}
     */
    @DeleteMapping("/task/{taskId}")
    public ResponseEntity<Void> deleteAllDependenciesForTask(@PathVariable Long taskId) {
        logger.info("REST request to delete all dependencies for task: {}", taskId);
        taskDependencyService.deleteAllDependenciesForTask(taskId);
        return ResponseEntity.noContent().build();
    }
}

// Made with Bob
