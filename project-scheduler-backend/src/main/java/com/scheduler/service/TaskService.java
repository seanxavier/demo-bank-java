package com.scheduler.service;

import com.scheduler.annotation.Auditable;
import com.scheduler.annotation.Loggable;
import com.scheduler.dto.TaskDTO;
import com.scheduler.exception.ResourceNotFoundException;
import com.scheduler.exception.ValidationException;
import com.scheduler.model.Project;
import com.scheduler.model.Task;
import com.scheduler.repository.ProjectRepository;
import com.scheduler.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing tasks.
 * Includes CRUD operations with story points validation and audit trail.
 */
@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    /**
     * Create a new task
     */
    @Loggable
    @Auditable(entityType = "Task", action = "CREATE")
    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO) {
        logger.info("Creating new task: {}", taskDTO.getName());

        // Validate story points (must be Fibonacci number)
        if (!Task.isValidFibonacci(taskDTO.getStoryPoints())) {
            throw new ValidationException(
                "Story points must be a valid Fibonacci number (1, 2, 3, 5, 8, 13, 21, 34, 55, 89)",
                "storyPoints",
                taskDTO.getStoryPoints()
            );
        }

        // Validate project exists
        Project project = projectRepository.findById(taskDTO.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", taskDTO.getProjectId()));

        // Check for duplicate task name in project
        if (taskRepository.existsByProjectIdAndName(taskDTO.getProjectId(), taskDTO.getName())) {
            throw new ValidationException(
                "Task with name '" + taskDTO.getName() + "' already exists in this project",
                "name",
                taskDTO.getName()
            );
        }

        Task task = convertToEntity(taskDTO, project);
        Task savedTask = taskRepository.save(task);

        logger.info("Task created successfully with ID: {}", savedTask.getId());
        return convertToDTO(savedTask);
    }

    /**
     * Get task by ID
     */
    @Loggable
    @Transactional(readOnly = true)
    public TaskDTO getTaskById(Long id) {
        logger.debug("Fetching task with ID: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));

        return convertToDTO(task);
    }

    /**
     * Get task by ID with dependencies
     */
    @Loggable
    @Transactional(readOnly = true)
    public TaskDTO getTaskWithDependencies(Long id) {
        logger.debug("Fetching task with dependencies, ID: {}", id);

        Task task = taskRepository.findByIdWithDependencies(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));

        return convertToDTO(task);
    }

    /**
     * Get all tasks
     */
    @Loggable
    @Transactional(readOnly = true)
    public List<TaskDTO> getAllTasks() {
        logger.debug("Fetching all tasks");

        List<Task> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get tasks by project ID
     */
    @Loggable
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByProjectId(Long projectId) {
        logger.debug("Fetching tasks for project ID: {}", projectId);

        // Verify project exists
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project", projectId);
        }

        List<Task> tasks = taskRepository.findByProjectId(projectId);
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get tasks by status
     */
    @Loggable
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByStatus(Task.TaskStatus status) {
        logger.debug("Fetching tasks with status: {}", status);

        List<Task> tasks = taskRepository.findByStatus(status);
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get tasks with no dependencies for a project
     */
    @Loggable
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksWithNoDependencies(Long projectId) {
        logger.debug("Fetching tasks with no dependencies for project ID: {}", projectId);

        List<Task> tasks = taskRepository.findTasksWithNoDependencies(projectId);
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update task
     */
    @Loggable
    @Auditable(entityType = "Task", action = "UPDATE")
    @Transactional
    public TaskDTO updateTask(Long id, TaskDTO taskDTO) {
        logger.info("Updating task with ID: {}", id);

        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));

        // Validate story points
        if (!Task.isValidFibonacci(taskDTO.getStoryPoints())) {
            throw new ValidationException(
                "Story points must be a valid Fibonacci number (1, 2, 3, 5, 8, 13, 21, 34, 55, 89)",
                "storyPoints",
                taskDTO.getStoryPoints()
            );
        }

        // Check for duplicate name (excluding current task)
        if (!existingTask.getName().equals(taskDTO.getName()) &&
            taskRepository.existsByProjectIdAndName(existingTask.getProject().getId(), taskDTO.getName())) {
            throw new ValidationException(
                "Task with name '" + taskDTO.getName() + "' already exists in this project",
                "name",
                taskDTO.getName()
            );
        }

        // Update fields
        existingTask.setName(taskDTO.getName());
        existingTask.setDaysRequired(taskDTO.getDaysRequired());
        existingTask.setStoryPoints(taskDTO.getStoryPoints());

        // Update status if provided
        if (taskDTO.getStatus() != null) {
            existingTask.setStatus(taskDTO.getStatus());
        }

        Task updatedTask = taskRepository.save(existingTask);

        logger.info("Task updated successfully: {}", updatedTask.getName());
        return convertToDTO(updatedTask);
    }

    /**
     * Delete task
     */
    @Loggable
    @Auditable(entityType = "Task", action = "DELETE")
    @Transactional
    public void deleteTask(Long id) {
        logger.info("Deleting task with ID: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));

        taskRepository.delete(task);

        logger.info("Task deleted successfully: {}", task.getName());
    }

    /**
     * Search tasks by name
     */
    @Loggable
    @Transactional(readOnly = true)
    public List<TaskDTO> searchTasksByName(String name) {
        logger.debug("Searching tasks by name: {}", name);

        List<Task> tasks = taskRepository.findByNameContainingIgnoreCase(name);
        return tasks.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Validate story points value
     */
    public boolean validateStoryPoints(Integer storyPoints) {
        return Task.isValidFibonacci(storyPoints);
    }

    /**
     * Get valid Fibonacci numbers for story points
     */
    public List<Integer> getValidStoryPoints() {
        return List.of(1, 2, 3, 5, 8, 13, 21, 34, 55, 89);
    }

    /**
     * Convert Task entity to DTO
     */
    private TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setName(task.getName());
        dto.setDaysRequired(task.getDaysRequired());
        dto.setStoryPoints(task.getStoryPoints());
        dto.setProjectId(task.getProject().getId());
        dto.setProjectName(task.getProject().getName());
        dto.setScheduledStart(task.getScheduledStart());
        dto.setScheduledEnd(task.getScheduledEnd());
        dto.setStatus(task.getStatus());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        return dto;
    }

    /**
     * Convert TaskDTO to entity
     */
    private Task convertToEntity(TaskDTO dto, Project project) {
        Task task = new Task();
        task.setName(dto.getName());
        task.setDaysRequired(dto.getDaysRequired());
        task.setStoryPoints(dto.getStoryPoints());
        task.setProject(project);
        task.setStatus(Task.TaskStatus.PENDING);
        return task;
    }
}

// Made with Bob
