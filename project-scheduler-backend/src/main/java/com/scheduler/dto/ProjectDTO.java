package com.scheduler.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Project entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {

    private Long id;

    @NotBlank(message = "Project name is required")
    private String name;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private String description;

    private List<TaskDTO> tasks = new ArrayList<>();

    private LocalDate createdAt;

    private LocalDate updatedAt;

    /**
     * Constructor without tasks (for simple project creation)
     */
    public ProjectDTO(Long id, String name, LocalDate startDate, String description) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.description = description;
        this.tasks = new ArrayList<>();
    }
}

// Made with Bob
