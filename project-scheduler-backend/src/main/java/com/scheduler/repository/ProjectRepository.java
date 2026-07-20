package com.scheduler.repository;

import com.scheduler.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Project entity.
 * Provides CRUD operations and custom queries.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * Find projects by name (case-insensitive)
     */
    List<Project> findByNameContainingIgnoreCase(String name);

    /**
     * Find projects starting after a specific date
     */
    List<Project> findByStartDateAfter(LocalDate date);

    /**
     * Find projects starting between two dates
     */
    List<Project> findByStartDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find project with all its tasks loaded
     */
    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.tasks WHERE p.id = :id")
    Optional<Project> findByIdWithTasks(Long id);

    /**
     * Check if a project exists by name
     */
    boolean existsByName(String name);
}

// Made with Bob
