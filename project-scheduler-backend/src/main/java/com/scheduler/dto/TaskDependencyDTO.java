package com.scheduler.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data Transfer Object for TaskDependency entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDependencyDTO {

    private Long id;

    @NotNull(message = "Task ID is required")
    private Long taskId;

    private String taskName;

    @NotNull(message = "Depends on task ID is required")
    private Long dependsOnTaskId;

    private String dependsOnTaskName;

    private LocalDate createdAt;

    /**
     * Constructor for creating a new dependency
     */
    public TaskDependencyDTO(Long taskId, Long dependsOnTaskId) {
        this.taskId = taskId;
        this.dependsOnTaskId = dependsOnTaskId;
    }
}

// Made with Bob
