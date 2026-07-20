package com.scheduler.controller;

import com.scheduler.dto.TaskDTO;
import com.scheduler.model.Task;
import com.scheduler.service.TaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Task management.
 * Provides CRUD endpoints for tasks with story points validation.
 */
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "http://localhost:4200")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    /**
     * Create a new task
     * POST /api/tasks
     */
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        logger.info("REST request to create task: {}", taskDTO.getName());
        TaskDTO created = taskService.createTask(taskDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Get task by ID
     * GET /api/tasks/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        logger.info("REST request to get task: {}", id);
        TaskDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    /**
     * Get task by ID with dependencies
     * GET /api/tasks/{id}/with-dependencies
     */
    @GetMapping("/{id}/with-dependencies")
    public ResponseEntity<TaskDTO> getTaskWithDependencies(@PathVariable Long id) {
        logger.info("REST request to get task with dependencies: {}", id);
        TaskDTO task = taskService.getTaskWithDependencies(id);
        return ResponseEntity.ok(task);
    }

    /**
     * Get all tasks
     * GET /api/tasks
     */
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        logger.info("REST request to get all tasks");
        List<TaskDTO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks by project ID
     * GET /api/tasks/project/{projectId}
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskDTO>> getTasksByProjectId(@PathVariable Long projectId) {
        logger.info("REST request to get tasks for project: {}", projectId);
        List<TaskDTO> tasks = taskService.getTasksByProjectId(projectId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks by status
     * GET /api/tasks/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDTO>> getTasksByStatus(@PathVariable String status) {
        logger.info("REST request to get tasks by status: {}", status);
        Task.TaskStatus taskStatus = Task.TaskStatus.valueOf(status.toUpperCase());
        List<TaskDTO> tasks = taskService.getTasksByStatus(taskStatus);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks with no dependencies for a project
     * GET /api/tasks/project/{projectId}/no-dependencies
     */
    @GetMapping("/project/{projectId}/no-dependencies")
    public ResponseEntity<List<TaskDTO>> getTasksWithNoDependencies(@PathVariable Long projectId) {
        logger.info("REST request to get tasks with no dependencies for project: {}", projectId);
        List<TaskDTO> tasks = taskService.getTasksWithNoDependencies(projectId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Search tasks by name
     * GET /api/tasks/search?name={name}
     */
    @GetMapping("/search")
    public ResponseEntity<List<TaskDTO>> searchTasks(@RequestParam String name) {
        logger.info("REST request to search tasks by name: {}", name);
        List<TaskDTO> tasks = taskService.searchTasksByName(name);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Update task
     * PUT /api/tasks/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskDTO taskDTO) {
        logger.info("REST request to update task: {}", id);
        TaskDTO updated = taskService.updateTask(id, taskDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete task
     * DELETE /api/tasks/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        logger.info("REST request to delete task: {}", id);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Validate story points
     * GET /api/tasks/validate-story-points/{points}
     */
    @GetMapping("/validate-story-points/{points}")
    public ResponseEntity<Boolean> validateStoryPoints(@PathVariable Integer points) {
        logger.info("REST request to validate story points: {}", points);
        boolean isValid = taskService.validateStoryPoints(points);
        return ResponseEntity.ok(isValid);
    }

    /**
     * Get valid story points (Fibonacci numbers)
     * GET /api/tasks/valid-story-points
     */
    @GetMapping("/valid-story-points")
    public ResponseEntity<List<Integer>> getValidStoryPoints() {
        logger.info("REST request to get valid story points");
        List<Integer> validPoints = taskService.getValidStoryPoints();
        return ResponseEntity.ok(validPoints);
    }
}

// Made with Bob
