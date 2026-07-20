import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Schedule } from '../models/project.model';
import { environment } from '../../environments/environment';

/**
 * Service for generating and managing schedules
 * Communicates with Spring Boot backend API
 */
@Injectable({
  providedIn: 'root'
})
export class ScheduleService {
  private apiUrl = `${environment.apiUrl}/schedule`;

  constructor(private http: HttpClient) {}

  /**
   * Generate schedule for a project
   * Uses ASAP (As Soon As Possible) scheduling algorithm
   */
  generateSchedule(projectId: number): Observable<Schedule> {
    return this.http.post<Schedule>(`${this.apiUrl}/project/${projectId}`, null);
  }

  /**
   * Get existing schedule for a project
   */
  getSchedule(projectId: number): Observable<Schedule> {
    return this.http.get<Schedule>(`${this.apiUrl}/project/${projectId}`);
  }

  /**
   * Health check endpoint
   */
  healthCheck(): Observable<{ status: string; timestamp: string }> {
    return this.http.get<{ status: string; timestamp: string }>(`${this.apiUrl}/health`);
  }

  /**
   * Calculate project duration in days
   */
  calculateProjectDuration(schedule: Schedule): number {
    if (!schedule || !schedule.tasks || schedule.tasks.length === 0) {
      return 0;
    }

    const startDate = new Date(schedule.projectStartDate);
    const endDates = schedule.tasks.map(task => new Date(task.scheduledEndDate));
    const maxEndDate = new Date(Math.max(...endDates.map(d => d.getTime())));

    const diffTime = Math.abs(maxEndDate.getTime() - startDate.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    return diffDays;
  }

  /**
   * Get critical path tasks (tasks with no slack time)
   */
  getCriticalPath(schedule: Schedule): number[] {
    // This is a simplified version
    // In a full implementation, you would calculate slack time for each task
    if (!schedule || !schedule.tasks) {
      return [];
    }

    // For now, return tasks that are on the longest path
    const taskMap = new Map(schedule.tasks.map(t => [t.taskId, t]));
    const criticalTasks: number[] = [];

    // Find the task with the latest end date
    const latestTask = schedule.tasks.reduce((latest, current) => {
      return new Date(current.scheduledEndDate) > new Date(latest.scheduledEndDate)
        ? current
        : latest;
    });

    criticalTasks.push(latestTask.taskId);

    return criticalTasks;
  }

  /**
   * Format date for display
   */
  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  /**
   * Calculate task progress percentage
   */
  calculateProgress(schedule: Schedule): number {
    if (!schedule || !schedule.tasks || schedule.tasks.length === 0) {
      return 0;
    }

    const completedTasks = schedule.tasks.filter(
      task => task.status === 'COMPLETED'
    ).length;

    return Math.round((completedTasks / schedule.tasks.length) * 100);
  }

  /**
   * Export schedule to CSV format
   */
  exportToCSV(schedule: Schedule): string {
    if (!schedule || !schedule.tasks) {
      return '';
    }

    const headers = [
      'Task ID',
      'Task Name',
      'Days Required',
      'Story Points',
      'Start Date',
      'End Date',
      'Dependencies',
      'Status'
    ];

    const rows = schedule.tasks.map(task => [
      task.taskId.toString(),
      task.taskName,
      task.daysRequired.toString(),
      task.storyPoints.toString(),
      task.scheduledStartDate,
      task.scheduledEndDate,
      task.dependencies.join('; '),
      task.status
    ]);

    const csvContent = [
      headers.join(','),
      ...rows.map(row => row.map(cell => `"${cell}"`).join(','))
    ].join('\n');

    return csvContent;
  }

  /**
   * Download schedule as CSV file
   */
  downloadScheduleCSV(schedule: Schedule): void {
    const csv = this.exportToCSV(schedule);
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `schedule-${schedule.projectName}-${new Date().toISOString().split('T')[0]}.csv`;
    link.click();
    window.URL.revokeObjectURL(url);
  }
}

// Made with Bob
