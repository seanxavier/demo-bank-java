package com.scheduler.controller;

import com.scheduler.dto.ProjectDTO;
import com.scheduler.service.ProjectService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Project management.
 * Provides CRUD endpoints for projects.
 */
@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "http://localhost:4200")
public class ProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private ProjectService projectService;

    /**
     * Create a new project
     * POST /api/projects
     */
    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectDTO projectDTO) {
        logger.info("REST request to create project: {}", projectDTO.getName());
        ProjectDTO created = projectService.createProject(projectDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Get project by ID
     * GET /api/projects/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        logger.info("REST request to get project: {}", id);
        ProjectDTO project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    /**
     * Get project by ID with all tasks
     * GET /api/projects/{id}/with-tasks
     */
    @GetMapping("/{id}/with-tasks")
    public ResponseEntity<ProjectDTO> getProjectWithTasks(@PathVariable Long id) {
        logger.info("REST request to get project with tasks: {}", id);
        ProjectDTO project = projectService.getProjectWithTasks(id);
        return ResponseEntity.ok(project);
    }

    /**
     * Get all projects
     * GET /api/projects
     */
    @GetMapping
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        logger.info("REST request to get all projects");
        List<ProjectDTO> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * Search projects by name
     * GET /api/projects/search?name={name}
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProjectDTO>> searchProjects(@RequestParam String name) {
        logger.info("REST request to search projects by name: {}", name);
        List<ProjectDTO> projects = projectService.searchProjectsByName(name);
        return ResponseEntity.ok(projects);
    }

    /**
     * Get projects starting after a specific date
     * GET /api/projects/starting-after?date={date}
     */
    @GetMapping("/starting-after")
    public ResponseEntity<List<ProjectDTO>> getProjectsStartingAfter(@RequestParam String date) {
        logger.info("REST request to get projects starting after: {}", date);
        LocalDate startDate = LocalDate.parse(date);
        List<ProjectDTO> projects = projectService.getProjectsStartingAfter(startDate);
        return ResponseEntity.ok(projects);
    }

    /**
     * Update project
     * PUT /api/projects/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectDTO projectDTO) {
        logger.info("REST request to update project: {}", id);
        ProjectDTO updated = projectService.updateProject(id, projectDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete project
     * DELETE /api/projects/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        logger.info("REST request to delete project: {}", id);
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}

// Made with Bob
