package com.scheduler.controller;

import com.scheduler.dto.ScheduleDTO;
import com.scheduler.service.SchedulingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Schedule generation.
 * Provides endpoint for generating project schedules using ASAP algorithm.
 */
@RestController
@RequestMapping("/api/schedule")
@CrossOrigin(origins = "http://localhost:4200")
public class ScheduleController {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleController.class);

    @Autowired
    private SchedulingService schedulingService;

    /**
     * Generate schedule for a project
     * GET /api/schedule/project/{projectId}
     * 
     * This endpoint:
     * 1. Validates no circular dependencies exist
     * 2. Performs topological sort on tasks
     * 3. Calculates start/end dates using ASAP algorithm
     * 4. Updates task scheduled dates in database
     * 5. Returns complete schedule with all tasks ordered
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<ScheduleDTO> generateSchedule(@PathVariable Long projectId) {
        logger.info("REST request to generate schedule for project: {}", projectId);
        
        try {
            ScheduleDTO schedule = schedulingService.generateSchedule(projectId);
            logger.info("Schedule generated successfully for project: {}", projectId);
            return ResponseEntity.ok(schedule);
            
        } catch (Exception e) {
            logger.error("Failed to generate schedule for project {}: {}", projectId, e.getMessage());
            
            // Return error schedule
            ScheduleDTO errorSchedule = new ScheduleDTO();
            errorSchedule.setProjectId(projectId);
            errorSchedule.setHasCircularDependencies(true);
            errorSchedule.setErrorMessage(e.getMessage());
            
            return ResponseEntity.badRequest().body(errorSchedule);
        }
    }

    /**
     * Regenerate schedule for a project (recalculate all dates)
     * POST /api/schedule/project/{projectId}/regenerate
     */
    @PostMapping("/project/{projectId}/regenerate")
    public ResponseEntity<ScheduleDTO> regenerateSchedule(@PathVariable Long projectId) {
        logger.info("REST request to regenerate schedule for project: {}", projectId);
        
        try {
            ScheduleDTO schedule = schedulingService.generateSchedule(projectId);
            logger.info("Schedule regenerated successfully for project: {}", projectId);
            return ResponseEntity.ok(schedule);
            
        } catch (Exception e) {
            logger.error("Failed to regenerate schedule for project {}: {}", projectId, e.getMessage());
            
            ScheduleDTO errorSchedule = new ScheduleDTO();
            errorSchedule.setProjectId(projectId);
            errorSchedule.setHasCircularDependencies(true);
            errorSchedule.setErrorMessage(e.getMessage());
            
            return ResponseEntity.badRequest().body(errorSchedule);
        }
    }

    /**
     * Health check endpoint
     * GET /api/schedule/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Schedule service is running");
    }
}

// Made with Bob
