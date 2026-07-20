package com.scheduler.service;

import com.scheduler.annotation.Auditable;
import com.scheduler.annotation.Loggable;
import com.scheduler.dto.ProjectDTO;
import com.scheduler.exception.ResourceNotFoundException;
import com.scheduler.exception.ValidationException;
import com.scheduler.model.Project;
import com.scheduler.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing projects.
 * Includes CRUD operations with audit trail.
 */
@Service
public class ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    private ProjectRepository projectRepository;

    /**
     * Create a new project
     */
    @Loggable
    @Auditable(entityType = "Project", action = "CREATE")
    @Transactional
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        logger.info("Creating new project: {}", projectDTO.getName());

        // Validate start date
        if (projectDTO.getStartDate().isBefore(LocalDate.now())) {
            throw new ValidationException(
                "Start date cannot be in the past",
                "startDate",
                projectDTO.getStartDate()
            );
        }

        // Check for duplicate name
        if (projectRepository.existsByName(projectDTO.getName())) {
            throw new ValidationException(
                "Project with name '" + projectDTO.getName() + "' already exists",
                "name",
                projectDTO.getName()
            );
        }

        Project project = convertToEntity(projectDTO);
        Project savedProject = projectRepository.save(project);

        logger.info("Project created successfully with ID: {}", savedProject.getId());
        return convertToDTO(savedProject);
    }

    /**
     * Get project by ID
     */
    @Loggable
    @Transactional(readOnly = true)
    public ProjectDTO getProjectById(Long id) {
        logger.debug("Fetching project with ID: {}", id);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));

        return convertToDTO(project);
    }

    /**
     * Get project by ID with all tasks
     */
    @Loggable
    @Transactional(readOnly = true)
    public ProjectDTO getProjectWithTasks(Long id) {
        logger.debug("Fetching project with tasks, ID: {}", id);

        Project project = projectRepository.findByIdWithTasks(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));

        return convertToDTO(project);
    }

    /**
     * Get all projects
     */
    @Loggable
    @Transactional(readOnly = true)
    public List<ProjectDTO> getAllProjects() {
        logger.debug("Fetching all projects");

        List<Project> projects = projectRepository.findAll();
        return projects.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search projects by name
     */
    @Loggable
    @Transactional(readOnly = true)
    public List<ProjectDTO> searchProjectsByName(String name) {
        logger.debug("Searching projects by name: {}", name);

        List<Project> projects = projectRepository.findByNameContainingIgnoreCase(name);
        return projects.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update project
     */
    @Loggable
    @Auditable(entityType = "Project", action = "UPDATE")
    @Transactional
    public ProjectDTO updateProject(Long id, ProjectDTO projectDTO) {
        logger.info("Updating project with ID: {}", id);

        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));

        // Validate start date
        if (projectDTO.getStartDate().isBefore(LocalDate.now())) {
            throw new ValidationException(
                "Start date cannot be in the past",
                "startDate",
                projectDTO.getStartDate()
            );
        }

        // Check for duplicate name (excluding current project)
        if (!existingProject.getName().equals(projectDTO.getName()) &&
            projectRepository.existsByName(projectDTO.getName())) {
            throw new ValidationException(
                "Project with name '" + projectDTO.getName() + "' already exists",
                "name",
                projectDTO.getName()
            );
        }

        // Update fields
        existingProject.setName(projectDTO.getName());
        existingProject.setStartDate(projectDTO.getStartDate());
        existingProject.setDescription(projectDTO.getDescription());

        Project updatedProject = projectRepository.save(existingProject);

        logger.info("Project updated successfully: {}", updatedProject.getName());
        return convertToDTO(updatedProject);
    }

    /**
     * Delete project
     */
    @Loggable
    @Auditable(entityType = "Project", action = "DELETE")
    @Transactional
    public void deleteProject(Long id) {
        logger.info("Deleting project with ID: {}", id);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));

        projectRepository.delete(project);

        logger.info("Project deleted successfully: {}", project.getName());
    }

    /**
     * Get projects starting after a specific date
     */
    @Loggable
    @Transactional(readOnly = true)
    public List<ProjectDTO> getProjectsStartingAfter(LocalDate date) {
        logger.debug("Fetching projects starting after: {}", date);

        List<Project> projects = projectRepository.findByStartDateAfter(date);
        return projects.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert Project entity to DTO
     */
    private ProjectDTO convertToDTO(Project project) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setStartDate(project.getStartDate());
        dto.setDescription(project.getDescription());
        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());
        return dto;
    }

    /**
     * Convert ProjectDTO to entity
     */
    private Project convertToEntity(ProjectDTO dto) {
        Project project = new Project();
        project.setName(dto.getName());
        project.setStartDate(dto.getStartDate());
        project.setDescription(dto.getDescription());
        return project;
    }
}

// Made with Bob
