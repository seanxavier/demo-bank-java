package com.scheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for project schedule output.
 * Contains the complete schedule with all tasks and their calculated dates.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {

    private Long projectId;
    
    private String projectName;
    
    private LocalDate projectStartDate;
    
    private LocalDate projectEndDate;
    
    private Integer totalTasks;
    
    private Integer totalDays;
    
    private Integer totalStoryPoints;
    
    private List<ScheduledTaskDTO> scheduledTasks = new ArrayList<>();
    
    private boolean hasCircularDependencies;
    
    private String errorMessage;

    /**
     * Nested DTO for individual scheduled tasks
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduledTaskDTO {
        
        private Long taskId;
        
        private String taskName;
        
        private Integer daysRequired;
        
        private Integer storyPoints;
        
        private LocalDate scheduledStart;
        
        private LocalDate scheduledEnd;
        
        private List<String> dependencies;
        
        private String status;
        
        /**
         * Calculate duration in days
         */
        public Integer getDuration() {
            if (scheduledStart != null && scheduledEnd != null) {
                return (int) java.time.temporal.ChronoUnit.DAYS.between(scheduledStart, scheduledEnd) + 1;
            }
            return daysRequired;
        }
    }

    /**
     * Add a scheduled task to the schedule
     */
    public void addScheduledTask(ScheduledTaskDTO task) {
        if (this.scheduledTasks == null) {
            this.scheduledTasks = new ArrayList<>();
        }
        this.scheduledTasks.add(task);
    }

    /**
     * Calculate project end date from scheduled tasks
     */
    public void calculateProjectEndDate() {
        if (scheduledTasks != null && !scheduledTasks.isEmpty()) {
            this.projectEndDate = scheduledTasks.stream()
                    .map(ScheduledTaskDTO::getScheduledEnd)
                    .filter(date -> date != null)
                    .max(LocalDate::compareTo)
                    .orElse(projectStartDate);
        }
    }

    /**
     * Calculate total days from start to end
     */
    public void calculateTotalDays() {
        if (projectStartDate != null && projectEndDate != null) {
            this.totalDays = (int) java.time.temporal.ChronoUnit.DAYS.between(projectStartDate, projectEndDate) + 1;
        }
    }

    /**
     * Calculate total story points
     */
    public void calculateTotalStoryPoints() {
        if (scheduledTasks != null) {
            this.totalStoryPoints = scheduledTasks.stream()
                    .map(ScheduledTaskDTO::getStoryPoints)
                    .filter(sp -> sp != null)
                    .mapToInt(Integer::intValue)
                    .sum();
        }
    }
}

// Made with Bob
