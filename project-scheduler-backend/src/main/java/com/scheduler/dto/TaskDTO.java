package com.scheduler.dto;

import com.scheduler.model.Task;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Task entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    private Long id;

    @NotBlank(message = "Task name is required")
    private String name;

    @NotNull(message = "Days required is mandatory")
    @Min(value = 1, message = "Days required must be at least 1")
    private Integer daysRequired;

    @NotNull(message = "Story points is required")
    @Min(value = 1, message = "Story points must be at least 1")
    private Integer storyPoints;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    private String projectName;

    private LocalDate scheduledStart;

    private LocalDate scheduledEnd;

    private Task.TaskStatus status;

    private List<Long> dependencyIds = new ArrayList<>();

    private List<String> dependencyNames = new ArrayList<>();

    private LocalDate createdAt;

    private LocalDate updatedAt;

    /**
     * Constructor for simple task creation
     */
    public TaskDTO(Long id, String name, Integer daysRequired, Integer storyPoints, Long projectId) {
        this.id = id;
        this.name = name;
        this.daysRequired = daysRequired;
        this.storyPoints = storyPoints;
        this.projectId = projectId;
        this.status = Task.TaskStatus.PENDING;
        this.dependencyIds = new ArrayList<>();
        this.dependencyNames = new ArrayList<>();
    }
}

// Made with Bob
