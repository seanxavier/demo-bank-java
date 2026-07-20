import { Component, OnInit, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { Schedule, ScheduledTask, Project } from '../../models/project.model';
import { ScheduleService } from '../../services/schedule.service';
import { ProjectService } from '../../services/project.service';

/**
 * Schedule View Component
 * Display generated project schedules in a table format
 */
@Component({
  selector: 'app-schedule-view',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatSelectModule,
    MatFormFieldModule
  ],
  templateUrl: './schedule-view.component.html',
  styleUrl: './schedule-view.component.scss'
})
export class ScheduleViewComponent implements OnInit {
  @Input() projectId?: number;

  projects: Project[] = [];
  selectedProjectId?: number;
  schedule?: Schedule;
  loading = false;
  loadingProjects = false;
  error?: string;

  // Table columns
  displayedColumns: string[] = [
    'taskName',
    'daysRequired',
    'storyPoints',
    'scheduledStartDate',
    'scheduledEndDate',
    'dependencies',
    'status'
  ];

  constructor(
    private scheduleService: ScheduleService,
    private projectService: ProjectService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadProjects();
    if (this.projectId) {
      this.selectedProjectId = this.projectId;
      this.loadSchedule();
    }
  }

  /**
   * Load all projects
   */
  loadProjects(): void {
    this.loadingProjects = true;
    this.projectService.getAllProjects().subscribe({
      next: (projects) => {
        this.projects = projects;
        this.loadingProjects = false;
      },
      error: (error) => {
        console.error('Error loading projects:', error);
        this.showMessage('Failed to load projects', 'error');
        this.loadingProjects = false;
      }
    });
  }

  /**
   * Handle project selection change
   */
  onProjectChange(projectId: number): void {
    this.selectedProjectId = projectId;
    this.schedule = undefined;
    this.error = undefined;
  }

  /**
   * Load or generate schedule for project
   */
  loadSchedule(): void {
    const projectId = this.selectedProjectId || this.projectId;
    if (!projectId) {
      this.error = 'No project selected';
      return;
    }

    this.loading = true;
    this.error = undefined;

    // Try to get existing schedule first
    this.scheduleService.getSchedule(projectId).subscribe({
      next: (schedule) => {
        this.schedule = schedule;
        this.loading = false;
      },
      error: () => {
        // If no schedule exists, generate one
        this.generateSchedule();
      }
    });
  }

  /**
   * Generate new schedule
   */
  generateSchedule(): void {
    const projectId = this.selectedProjectId || this.projectId;
    if (!projectId) {
      this.error = 'No project selected';
      return;
    }

    this.loading = true;
    this.error = undefined;

    this.scheduleService.generateSchedule(projectId).subscribe({
      next: (schedule) => {
        this.schedule = schedule;
        this.loading = false;
        this.showMessage('Schedule generated successfully', 'success');
      },
      error: (error) => {
        console.error('Error generating schedule:', error);
        const errorMessage = error.error?.message || 'Failed to generate schedule';
        this.error = errorMessage;
        this.loading = false;
        this.showMessage(errorMessage, 'error');
      }
    });
  }

  /**
   * Refresh schedule
   */
  refreshSchedule(): void {
    this.generateSchedule();
  }

  /**
   * Export schedule to CSV
   */
  exportToCSV(): void {
    if (!this.schedule) {
      this.showMessage('No schedule to export', 'warning');
      return;
    }

    try {
      this.scheduleService.downloadScheduleCSV(this.schedule);
      this.showMessage('Schedule exported successfully', 'success');
    } catch (error) {
      console.error('Error exporting schedule:', error);
      this.showMessage('Failed to export schedule', 'error');
    }
  }

  /**
   * Format date for display
   */
  formatDate(dateString: string): string {
    return this.scheduleService.formatDate(dateString);
  }

  /**
   * Get progress percentage
   */
  getProgress(): number {
    if (!this.schedule) return 0;
    return this.scheduleService.calculateProgress(this.schedule);
  }

  /**
   * Get status color
   */
  getStatusColor(status: string): string {
    switch (status) {
      case 'COMPLETED':
        return 'success';
      case 'IN_PROGRESS':
        return 'primary';
      case 'BLOCKED':
        return 'warn';
      default:
        return 'default';
    }
  }

  /**
   * Get status icon
   */
  getStatusIcon(status: string): string {
    switch (status) {
      case 'COMPLETED':
        return 'check_circle';
      case 'IN_PROGRESS':
        return 'pending';
      case 'BLOCKED':
        return 'block';
      default:
        return 'radio_button_unchecked';
    }
  }

  /**
   * Calculate task duration
   */
  getTaskDuration(task: ScheduledTask): number {
    const start = new Date(task.scheduledStartDate);
    const end = new Date(task.scheduledEndDate);
    const diffTime = Math.abs(end.getTime() - start.getTime());
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  }

  /**
   * Check if task is on critical path
   */
  isOnCriticalPath(task: ScheduledTask): boolean {
    if (!this.schedule) return false;
    const criticalPath = this.scheduleService.getCriticalPath(this.schedule);
    return criticalPath.includes(task.taskId);
  }

  /**
   * Get total project duration
   */
  getTotalDuration(): number {
    if (!this.schedule) return 0;
    return this.scheduleService.calculateProjectDuration(this.schedule);
  }

  /**
   * Get total story points
   */
  getTotalStoryPoints(): number {
    if (!this.schedule || !this.schedule.tasks) return 0;
    return this.schedule.tasks.reduce((sum, task) => sum + task.storyPoints, 0);
  }

  /**
   * Get completed story points
   */
  getCompletedStoryPoints(): number {
    if (!this.schedule || !this.schedule.tasks) return 0;
    return this.schedule.tasks
      .filter(task => task.status === 'COMPLETED')
      .reduce((sum, task) => sum + task.storyPoints, 0);
  }

  /**
   * Show snackbar message
   */
  private showMessage(message: string, type: 'success' | 'error' | 'warning'): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      horizontalPosition: 'end',
      verticalPosition: 'top',
      panelClass: [`snackbar-${type}`]
    });
  }

  /**
   * Get dependency tooltip text
   */
  getDependencyTooltip(dependencies: string[]): string {
    if (!dependencies || dependencies.length === 0) {
      return 'No dependencies';
    }
    return `Depends on: ${dependencies.join(', ')}`;
  }

  /**
   * Check if schedule has tasks
   */
  hasTasks(): boolean {
    return !!(this.schedule && this.schedule.tasks && this.schedule.tasks.length > 0);
  }
}

// Made with Bob
