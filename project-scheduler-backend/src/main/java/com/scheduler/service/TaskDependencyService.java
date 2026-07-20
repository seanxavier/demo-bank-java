package com.scheduler.service;

import com.scheduler.annotation.Auditable;
import com.scheduler.annotation.Loggable;
import com.scheduler.dto.TaskDependencyDTO;
import com.scheduler.exception.CircularDependencyException;
import com.scheduler.exception.ResourceNotFoundException;
import com.scheduler.exception.ValidationException;
import com.scheduler.model.Task;
import com.scheduler.model.TaskDependency;
import com.scheduler.repository.TaskDependencyRepository;
import com.scheduler.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing task dependencies.
 * Includes circular dependency validation and audit trail.
 */
@Service
public class TaskDependencyService {

    private static final Logger logger = LoggerFactory.getLogger(TaskDependencyService.class);

    @Autowired
    private TaskDependencyRepository taskDependencyRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SchedulingService schedulingService;

    /**
     * Create a new task dependency
     */
    @Loggable
    @Auditable(entityType = "TaskDependency", action = "CREATE")
    @Transactional
    public TaskDependencyDTO createDependency(TaskDependencyDTO dependencyDTO) {
        logger.info("Creating dependency: Task {} depends on Task {}", 
                   dependencyDTO.getTaskId(), dependencyDTO.getDependsOnTaskId());

        // Validate tasks exist
        Task task = taskRepository.findById(dependencyDTO.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task", dependencyDTO.getTaskId()));

        Task dependsOnTask = taskRepository.findById(dependencyDTO.getDependsOnTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task", dependencyDTO.getDependsOnTaskId()));

        // Validate tasks belong to same project
        if (!task.getProject().getId().equals(dependsOnTask.getProject().getId())) {
            throw new ValidationException(
                "Tasks must belong to the same project",
                "projectId",
                task.getProject().getId()
            );
        }

        // Validate not self-referencing
        if (task.getId().equals(dependsOnTask.getId())) {
            throw new ValidationException(
                "A task cannot depend on itself",
                "dependsOnTaskId",
                dependsOnTask.getId()
            );
        }

        // Check if dependency already exists
        if (taskDependencyRepository.existsByTaskIdAndDependsOnTaskId(
                task.getId(), dependsOnTask.getId())) {
            throw new ValidationException(
                "This dependency already exists",
                "dependency",
                String.format("%d -> %d", task.getId(), dependsOnTask.getId())
            );
        }

        // Check for circular dependencies BEFORE creating
        validateNoCircularDependency(task.getId(), dependsOnTask.getId());

        TaskDependency dependency = new TaskDependency(task, dependsOnTask);
        TaskDependency savedDependency = taskDependencyRepository.save(dependency);

        logger.info("Dependency created successfully with ID: {}", savedDependency.getId());
        return convertToDTO(savedDependency);
    }

    /**
     * Validate that adding this dependency won't create a circular dependency
     */
    @Loggable
    public void validateNoCircularDependency(Long taskId, Long dependsOnTaskId) {
        logger.debug("Validating no circular dependency for: {} -> {}", taskId, dependsOnTaskId);

        // Get all existing dependencies for the project
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));

        List<TaskDependency> existingDependencies = 
            taskDependencyRepository.findByProjectId(task.getProject().getId());

        // Build temporary graph including the new dependency
        Map<Long, List<Long>> graph = buildDependencyGraph(existingDependencies);
        
        // Add the new dependency to the graph
        graph.computeIfAbsent(dependsOnTaskId, k -> new ArrayList<>()).add(taskId);

        // Check for cycles using DFS
        Map<Long, Integer> visited = new HashMap<>();
        List<Task> projectTasks = taskRepository.findByProjectId(task.getProject().getId());
        
        for (Task t : projectTasks) {
            visited.put(t.getId(), 0);
        }

        if (hasCycleDFS(taskId, graph, visited, new ArrayList<>())) {
            throw new CircularDependencyException(
                String.format("Adding this dependency would create a circular dependency: Task %d -> Task %d", 
                             taskId, dependsOnTaskId),
                taskId,
                dependsOnTaskId
            );
        }

        logger.debug("No circular dependency detected");
    }

    /**
     * DFS to detect cycles
     */
    private boolean hasCycleDFS(Long taskId, Map<Long, List<Long>> graph, 
                                Map<Long, Integer> visited, List<Long> path) {
        if (!visited.containsKey(taskId)) {
            return false; // Task not in this project
        }

        visited.put(taskId, 1); // Mark as visiting
        path.add(taskId);

        List<Long> neighbors = graph.getOrDefault(taskId, Collections.emptyList());
        for (Long neighbor : neighbors) {
            if (!visited.containsKey(neighbor)) {
                continue; // Skip if not in project
            }
            
            if (visited.get(neighbor) == 1) {
                // Back edge found - cycle detected
                logger.error("Circular dependency detected: {} -> {}", taskId, neighbor);
                logger.error("Path: {}", path);
                return true;
            }
            
            if (visited.get(neighbor) == 0) {
                if (hasCycleDFS(neighbor, graph, visited, path)) {
                    return true;
                }
            }
        }

        visited.put(taskId, 2); // Mark as visited
        path.remove(taskId);
        return false;
    }

    /**
     * Build dependency graph (adjacency list)
     */
    private Map<Long, List<Long>> buildDependencyGraph(List<TaskDependency> dependencies) {
        Map<Long, List<Long>> graph = new HashMap<>();

        for (TaskDependency dep : dependencies) {
            Long dependsOnId = dep.getDependsOnTask().getId();
            Long taskId = dep.getTask().getId();
            graph.computeIfAbsent(dependsOnId, k -> new ArrayList<>()).add(taskId);
        }

        return graph;
    }

    /**
     * Get dependency by ID
     */
    @Loggable
    @Transactional(readOnly = true)
    public TaskDependencyDTO getDependencyById(Long id) {
        logger.debug("Fetching dependency with ID: {}", id);

        TaskDependency dependency = taskDependencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskDependency", id));

        return convertToDTO(dependency);
    }

    /**
     * Get all dependencies for a task
     */
    @Loggable
    @Transactional(readOnly = true)
    public List<TaskDependencyDTO> getDependenciesForTask(Long taskId) {
        logger.debug("Fetching dependencies for task ID: {}", taskId);

        // Verify task exists
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task", taskId);
        }

        List<TaskDependency> dependencies = taskDependencyRepository.findByTaskId(taskId);
        return dependencies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all tasks that depend on a specific task
     */
    @Loggable
    @Transactional(readOnly = true)
    public List<TaskDependencyDTO> getTasksThatDependOn(Long taskId) {
        logger.debug("Fetching tasks that depend on task ID: {}", taskId);

        // Verify task exists
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task", taskId);
        }

        List<TaskDependency> dependencies = taskDependencyRepository.findByDependsOnTaskId(taskId);
        return dependencies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all dependencies for a project
     */
    @Loggable
    @Transactional(readOnly = true)
    public List<TaskDependencyDTO> getDependenciesByProjectId(Long projectId) {
        logger.debug("Fetching dependencies for project ID: {}", projectId);

        List<TaskDependency> dependencies = taskDependencyRepository.findByProjectId(projectId);
        return dependencies.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Delete dependency
     */
    @Loggable
    @Auditable(entityType = "TaskDependency", action = "DELETE")
    @Transactional
    public void deleteDependency(Long id) {
        logger.info("Deleting dependency with ID: {}", id);

        TaskDependency dependency = taskDependencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TaskDependency", id));

        taskDependencyRepository.delete(dependency);

        logger.info("Dependency deleted successfully");
    }

    /**
     * Delete all dependencies for a task
     */
    @Loggable
    @Auditable(entityType = "TaskDependency", action = "DELETE_ALL")
    @Transactional
    public void deleteAllDependenciesForTask(Long taskId) {
        logger.info("Deleting all dependencies for task ID: {}", taskId);

        // Verify task exists
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task", taskId);
        }

        taskDependencyRepository.deleteByTaskId(taskId);

        logger.info("All dependencies deleted for task ID: {}", taskId);
    }

    /**
     * Convert TaskDependency entity to DTO
     */
    private TaskDependencyDTO convertToDTO(TaskDependency dependency) {
        TaskDependencyDTO dto = new TaskDependencyDTO();
        dto.setId(dependency.getId());
        dto.setTaskId(dependency.getTask().getId());
        dto.setTaskName(dependency.getTask().getName());
        dto.setDependsOnTaskId(dependency.getDependsOnTask().getId());
        dto.setDependsOnTaskName(dependency.getDependsOnTask().getName());
        dto.setCreatedAt(dependency.getCreatedAt());
        return dto;
    }
}

// Made with Bob
