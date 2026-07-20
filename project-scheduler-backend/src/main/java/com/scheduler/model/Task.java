package com.scheduler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
 * Task entity representing a task within a project.
 * Story points must be a Fibonacci number (1, 2, 3, 5, 8, 13, 21, etc.)
 */
@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Task name is required")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Days required is mandatory")
    @Min(value = 1, message = "Days required must be at least 1")
    @Column(nullable = false)
    private Integer daysRequired;

    @NotNull(message = "Story points is required")
    @Min(value = 1, message = "Story points must be at least 1")
    @Column(nullable = false)
    private Integer storyPoints;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnore
    private Project project;

    @Column
    private LocalDate scheduledStart;

    @Column
    private LocalDate scheduledEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.PENDING;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TaskDependency> dependencies = new ArrayList<>();

    @OneToMany(mappedBy = "dependsOnTask", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TaskDependency> dependentTasks = new ArrayList<>();

    @Column(updatable = false)
    private LocalDate createdAt;

    private LocalDate updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
        if (status == null) {
            status = TaskStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDate.now();
    }

    /**
     * Task status enum
     */
    public enum TaskStatus {
        PENDING,
        SCHEDULED,
        IN_PROGRESS,
        COMPLETED,
        BLOCKED
    }

    /**
     * Check if story points is a valid Fibonacci number
     */
    public static boolean isValidFibonacci(int value) {
        if (value <= 0) return false;
        
        // Common Fibonacci numbers used in Agile: 1, 2, 3, 5, 8, 13, 21, 34, 55, 89
        int[] fibonacci = {1, 2, 3, 5, 8, 13, 21, 34, 55, 89};
        for (int fib : fibonacci) {
            if (value == fib) return true;
        }
        return false;
    }
}

// Made with Bob
