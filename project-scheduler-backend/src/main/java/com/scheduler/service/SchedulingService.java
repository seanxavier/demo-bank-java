package com.scheduler.service;

import com.scheduler.annotation.Loggable;
import com.scheduler.dto.ScheduleDTO;
import com.scheduler.exception.CircularDependencyException;
import com.scheduler.model.Project;
import com.scheduler.model.Task;
import com.scheduler.model.TaskDependency;
import com.scheduler.repository.ProjectRepository;
import com.scheduler.repository.TaskDependencyRepository;
import com.scheduler.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for scheduling tasks using ASAP (As Soon As Possible) algorithm.
 * Implements topological sort and circular dependency detection.
 */
@Service
public class SchedulingService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulingService.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskDependencyRepository taskDependencyRepository;

    /**
     * Generate schedule for a project using ASAP algorithm
     */
    @Loggable
    @Transactional
    public ScheduleDTO generateSchedule(Long projectId) {
        logger.info("Generating schedule for project ID: {}", projectId);

        Project project = projectRepository.findByIdWithTasks(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));

        List<Task> tasks = taskRepository.findByProjectIdWithDependencies(projectId);
        List<TaskDependency> dependencies = taskDependencyRepository.findByProjectId(projectId);

        // Check for circular dependencies
        detectCircularDependencies(tasks, dependencies);

        // Perform topological sort
        List<Task> sortedTasks = topologicalSort(tasks, dependencies);

        // Calculate scheduled dates using ASAP algorithm
        Map<Long, LocalDate> startDates = new HashMap<>();
        Map<Long, LocalDate> endDates = new HashMap<>();

        for (Task task : sortedTasks) {
            LocalDate startDate = calculateStartDate(task, dependencies, endDates, project.getStartDate());
            LocalDate endDate = startDate.plusDays(task.getDaysRequired() - 1);

            startDates.put(task.getId(), startDate);
            endDates.put(task.getId(), endDate);

            // Update task with scheduled dates
            task.setScheduledStart(startDate);
            task.setScheduledEnd(endDate);
            task.setStatus(Task.TaskStatus.SCHEDULED);
        }

        // Save updated tasks
        taskRepository.saveAll(sortedTasks);

        // Build schedule DTO
        ScheduleDTO schedule = buildScheduleDTO(project, sortedTasks, dependencies);

        logger.info("Schedule generated successfully for project: {}", project.getName());
        return schedule;
    }

    /**
     * Detect circular dependencies using DFS
     */
    @Loggable
    public void detectCircularDependencies(List<Task> tasks, List<TaskDependency> dependencies) {
        logger.debug("Checking for circular dependencies...");

        // Build adjacency list
        Map<Long, List<Long>> graph = buildDependencyGraph(dependencies);

        // Track visited nodes: 0 = unvisited, 1 = visiting, 2 = visited
        Map<Long, Integer> visited = new HashMap<>();
        for (Task task : tasks) {
            visited.put(task.getId(), 0);
        }

        // Perform DFS for each unvisited node
        for (Task task : tasks) {
            if (visited.get(task.getId()) == 0) {
                if (hasCycleDFS(task.getId(), graph, visited, new ArrayList<>())) {
                    throw new CircularDependencyException(
                            "Circular dependency detected in project tasks",
                            task.getId(),
                            null
                    );
                }
            }
        }

        logger.debug("No circular dependencies found");
    }

    /**
     * DFS to detect cycles
     */
    private boolean hasCycleDFS(Long taskId, Map<Long, List<Long>> graph, 
                                Map<Long, Integer> visited, List<Long> path) {
        visited.put(taskId, 1); // Mark as visiting
        path.add(taskId);

        List<Long> neighbors = graph.getOrDefault(taskId, Collections.emptyList());
        for (Long neighbor : neighbors) {
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
     * Topological sort using Kahn's algorithm
     */
    @Loggable
    public List<Task> topologicalSort(List<Task> tasks, List<TaskDependency> dependencies) {
        logger.debug("Performing topological sort on {} tasks", tasks.size());

        // Build adjacency list and in-degree map
        Map<Long, List<Long>> graph = buildDependencyGraph(dependencies);
        Map<Long, Integer> inDegree = new HashMap<>();
        Map<Long, Task> taskMap = tasks.stream()
                .collect(Collectors.toMap(Task::getId, t -> t));

        // Initialize in-degree
        for (Task task : tasks) {
            inDegree.put(task.getId(), 0);
        }

        // Calculate in-degree
        for (TaskDependency dep : dependencies) {
            inDegree.put(dep.getTask().getId(), 
                        inDegree.get(dep.getTask().getId()) + 1);
        }

        // Queue for tasks with no dependencies
        Queue<Long> queue = new LinkedList<>();
        for (Task task : tasks) {
            if (inDegree.get(task.getId()) == 0) {
                queue.offer(task.getId());
            }
        }

        List<Task> sorted = new ArrayList<>();

        while (!queue.isEmpty()) {
            Long taskId = queue.poll();
            sorted.add(taskMap.get(taskId));

            // Reduce in-degree for dependent tasks
            List<Long> dependents = graph.getOrDefault(taskId, Collections.emptyList());
            for (Long dependent : dependents) {
                inDegree.put(dependent, inDegree.get(dependent) - 1);
                if (inDegree.get(dependent) == 0) {
                    queue.offer(dependent);
                }
            }
        }

        if (sorted.size() != tasks.size()) {
            throw new CircularDependencyException("Circular dependency detected during topological sort");
        }

        logger.debug("Topological sort completed. Order: {}", 
                    sorted.stream().map(Task::getName).collect(Collectors.toList()));

        return sorted;
    }

    /**
     * Calculate start date for a task using ASAP algorithm
     */
    private LocalDate calculateStartDate(Task task, List<TaskDependency> dependencies,
                                        Map<Long, LocalDate> endDates, LocalDate projectStartDate) {
        // Get all dependencies for this task
        List<TaskDependency> taskDeps = dependencies.stream()
                .filter(dep -> dep.getTask().getId().equals(task.getId()))
                .collect(Collectors.toList());

        if (taskDeps.isEmpty()) {
            // No dependencies - start on project start date
            return projectStartDate;
        }

        // Start after all dependencies complete
        LocalDate latestEndDate = taskDeps.stream()
                .map(dep -> endDates.get(dep.getDependsOnTask().getId()))
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(projectStartDate);

        // Start the next day after the latest dependency ends
        return latestEndDate.plusDays(1);
    }

    /**
     * Build dependency graph (adjacency list)
     * Key: task ID, Value: list of tasks that depend on this task
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
     * Build schedule DTO from calculated schedule
     */
    private ScheduleDTO buildScheduleDTO(Project project, List<Task> tasks, 
                                        List<TaskDependency> dependencies) {
        ScheduleDTO schedule = new ScheduleDTO();
        schedule.setProjectId(project.getId());
        schedule.setProjectName(project.getName());
        schedule.setProjectStartDate(project.getStartDate());
        schedule.setTotalTasks(tasks.size());

        // Build scheduled tasks
        for (Task task : tasks) {
            List<String> deps = dependencies.stream()
                    .filter(dep -> dep.getTask().getId().equals(task.getId()))
                    .map(dep -> dep.getDependsOnTask().getName())
                    .collect(Collectors.toList());

            ScheduleDTO.ScheduledTaskDTO scheduledTask = new ScheduleDTO.ScheduledTaskDTO(
                    task.getId(),
                    task.getName(),
                    task.getDaysRequired(),
                    task.getStoryPoints(),
                    task.getScheduledStart(),
                    task.getScheduledEnd(),
                    deps,
                    task.getStatus().name()
            );

            schedule.addScheduledTask(scheduledTask);
        }

        // Calculate totals
        schedule.calculateProjectEndDate();
        schedule.calculateTotalDays();
        schedule.calculateTotalStoryPoints();

        return schedule;
    }
}

// Made with Bob
