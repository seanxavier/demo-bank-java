package com.scheduler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * TaskDependency entity representing a dependency relationship between tasks.
 * A task can depend on another task (must complete before this task can start).
 */
@Entity
@Table(name = "task_dependencies", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"task_id", "depends_on_task_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDependency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    @NotNull(message = "Task is required")
    @JsonIgnore
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depends_on_task_id", nullable = false)
    @NotNull(message = "Dependency task is required")
    @JsonIgnore
    private Task dependsOnTask;

    @Column(updatable = false)
    private LocalDate createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }

    /**
     * Constructor for creating a dependency relationship
     */
    public TaskDependency(Task task, Task dependsOnTask) {
        this.task = task;
        this.dependsOnTask = dependsOnTask;
    }
}

// Made with Bob
