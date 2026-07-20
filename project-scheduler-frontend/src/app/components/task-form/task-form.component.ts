import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { Task, Project, VALID_STORY_POINTS } from '../../models/project.model';
import { TaskService } from '../../services/task.service';
import { ProjectService } from '../../services/project.service';
import { DependencyService } from '../../services/dependency.service';
import { forkJoin } from 'rxjs';

/**
 * Task Form Component
 * Create and edit tasks with Fibonacci story points and dependency selection
 */
@Component({
  selector: 'app-task-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatCardModule,
    MatSnackBarModule,
    MatChipsModule,
    MatIconModule
  ],
  templateUrl: './task-form.component.html',
  styleUrl: './task-form.component.scss'
})
export class TaskFormComponent implements OnInit {
  @Input() task?: Task;
  @Input() projectId?: number;
  @Input() mode: 'create' | 'edit' = 'create';
  @Output() taskSaved = new EventEmitter<Task>();
  @Output() cancelled = new EventEmitter<void>();

  taskForm!: FormGroup;
  loading = false;
  validatingDependencies = false;

  // Data for dropdowns
  projects: Project[] = [];
  availableTasks: Task[] = [];
  storyPoints = VALID_STORY_POINTS;
  selectedDependencies: number[] = [];

  constructor(
    private fb: FormBuilder,
    private taskService: TaskService,
    private projectService: ProjectService,
    private dependencyService: DependencyService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadProjects();
    
    if (this.task && this.mode === 'edit') {
      this.loadTask();
    } else if (this.projectId) {
      this.taskForm.patchValue({ projectId: this.projectId });
      this.loadAvailableTasks(this.projectId);
    }
  }

  /**
   * Initialize form with validators
   */
  private initForm(): void {
    this.taskForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      daysRequired: [1, [Validators.required, Validators.min(1), Validators.max(365)]],
      storyPoints: [1, Validators.required],
      projectId: ['', Validators.required],
      dependencies: [[]]
    });

    // Load available tasks when project changes
    this.taskForm.get('projectId')?.valueChanges.subscribe(projectId => {
      if (projectId) {
        this.loadAvailableTasks(projectId);
      }
    });
  }

  /**
   * Load all projects for dropdown
   */
  private loadProjects(): void {
    this.projectService.getAllProjects().subscribe({
      next: (projects) => {
        this.projects = projects;
      },
      error: (error) => {
        console.error('Error loading projects:', error);
        this.showMessage('Failed to load projects', 'error');
      }
    });
  }

  /**
   * Load available tasks for dependency selection
   */
  private loadAvailableTasks(projectId: number): void {
    this.taskService.getTasksByProjectId(projectId).subscribe({
      next: (tasks) => {
        // Exclude current task from available dependencies
        this.availableTasks = tasks.filter(t => t.id !== this.task?.id);
      },
      error: (error) => {
        console.error('Error loading tasks:', error);
        this.showMessage('Failed to load tasks', 'error');
      }
    });
  }

  /**
   * Load task data for editing
   */
  private loadTask(): void {
    if (this.task) {
      this.taskForm.patchValue({
        name: this.task.name,
        daysRequired: this.task.daysRequired,
        storyPoints: this.task.storyPoints,
        projectId: this.task.projectId
      });

      // Load dependencies
      if (this.task.id) {
        this.dependencyService.getDependenciesForTask(this.task.id).subscribe({
          next: (dependencies) => {
            this.selectedDependencies = dependencies.map(d => d.dependsOnTaskId);
            this.taskForm.patchValue({ dependencies: this.selectedDependencies });
          },
          error: (error) => {
            console.error('Error loading dependencies:', error);
          }
        });
      }

      this.loadAvailableTasks(this.task.projectId);
    }
  }

  /**
   * Validate dependencies for circular references
   */
  async validateDependencies(dependencyIds: number[]): Promise<boolean> {
    if (!dependencyIds || dependencyIds.length === 0) {
      return true;
    }

    const taskId = this.task?.id;
    if (!taskId) {
      // For new tasks, we can't validate circular dependencies yet
      return true;
    }

    this.validatingDependencies = true;

    try {
      // Check each dependency for circular reference
      const validationPromises = dependencyIds.map(depId =>
        this.dependencyService.validateNoCircularDependency(taskId, depId).toPromise()
      );

      const results = await Promise.all(validationPromises);
      const allValid = results.every(result => result === true);

      if (!allValid) {
        this.showMessage('Circular dependency detected! Please remove conflicting dependencies.', 'error');
        return false;
      }

      return true;
    } catch (error) {
      console.error('Error validating dependencies:', error);
      this.showMessage('Failed to validate dependencies', 'error');
      return false;
    } finally {
      this.validatingDependencies = false;
    }
  }

  /**
   * Handle dependency selection change
   */
  async onDependencyChange(selectedIds: number[]): Promise<void> {
    this.selectedDependencies = selectedIds;
    
    if (this.mode === 'edit' && this.task?.id) {
      const isValid = await this.validateDependencies(selectedIds);
      if (!isValid) {
        // Revert to previous selection
        this.taskForm.patchValue({ 
          dependencies: this.selectedDependencies.filter(id => !selectedIds.includes(id))
        });
      }
    }
  }

  /**
   * Remove a dependency from the selected list
   */
  removeDependency(depId: number): void {
    const updatedDependencies = this.selectedDependencies.filter(id => id !== depId);
    this.onDependencyChange(updatedDependencies);
  }

  /**
   * Submit form
   */
  async onSubmit(): Promise<void> {
    if (this.taskForm.invalid) {
      this.markFormGroupTouched(this.taskForm);
      this.showMessage('Please fix form errors', 'error');
      return;
    }

    // Validate dependencies
    const dependencies = this.taskForm.get('dependencies')?.value || [];
    if (this.mode === 'edit' && this.task?.id) {
      const isValid = await this.validateDependencies(dependencies);
      if (!isValid) {
        return;
      }
    }

    this.loading = true;
    const formValue = this.taskForm.value;
    
    const taskData: Task = {
      name: formValue.name,
      daysRequired: formValue.daysRequired,
      storyPoints: formValue.storyPoints,
      projectId: formValue.projectId,
      dependencies: dependencies
    };

    if (this.mode === 'edit' && this.task?.id) {
      this.updateTask(this.task.id, taskData);
    } else {
      this.createTask(taskData);
    }
  }

  /**
   * Create new task
   */
  private createTask(task: Task): void {
    this.taskService.createTask(task).subscribe({
      next: (created) => {
        // Create dependencies if any
        if (task.dependencies && task.dependencies.length > 0 && created.id) {
          this.createDependencies(created.id, task.dependencies);
        } else {
          this.showMessage('Task created successfully', 'success');
          this.taskSaved.emit(created);
          this.taskForm.reset();
        }
      },
      error: (error) => {
        console.error('Error creating task:', error);
        this.showMessage(error.error?.message || 'Failed to create task', 'error');
        this.loading = false;
      }
    });
  }

  /**
   * Update existing task
   */
  private updateTask(id: number, task: Task): void {
    this.taskService.updateTask(id, task).subscribe({
      next: (updated) => {
        // Update dependencies
        if (task.dependencies && updated.id) {
          this.updateDependencies(updated.id, task.dependencies);
        } else {
          this.showMessage('Task updated successfully', 'success');
          this.taskSaved.emit(updated);
        }
      },
      error: (error) => {
        console.error('Error updating task:', error);
        this.showMessage(error.error?.message || 'Failed to update task', 'error');
        this.loading = false;
      }
    });
  }

  /**
   * Create task dependencies
   */
  private createDependencies(taskId: number, dependencyIds: number[]): void {
    const createObs = dependencyIds.map(depId =>
      this.dependencyService.createDependency({
        taskId: taskId,
        dependsOnTaskId: depId
      })
    );

    forkJoin(createObs).subscribe({
      next: () => {
        this.showMessage('Task and dependencies created successfully', 'success');
        this.taskSaved.emit({ id: taskId } as Task);
        this.taskForm.reset();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error creating dependencies:', error);
        this.showMessage('Task created but failed to create some dependencies', 'warning');
        this.loading = false;
      }
    });
  }

  /**
   * Update task dependencies
   */
  private updateDependencies(taskId: number, newDependencyIds: number[]): void {
    // Get current dependencies
    this.dependencyService.getDependenciesForTask(taskId).subscribe({
      next: (currentDeps) => {
        const currentIds = currentDeps.map(d => d.dependsOnTaskId);
        
        // Find dependencies to add and remove
        const toAdd = newDependencyIds.filter(id => !currentIds.includes(id));
        const toRemove = currentIds.filter(id => !newDependencyIds.includes(id));

        const operations = [
          ...toAdd.map(depId => 
            this.dependencyService.createDependency({ taskId, dependsOnTaskId: depId })
          ),
          ...toRemove.map(depId =>
            this.dependencyService.deleteDependencyByTaskIds(taskId, depId)
          )
        ];

        if (operations.length > 0) {
          forkJoin(operations).subscribe({
            next: () => {
              this.showMessage('Task updated successfully', 'success');
              this.taskSaved.emit({ id: taskId } as Task);
              this.loading = false;
            },
            error: (error) => {
              console.error('Error updating dependencies:', error);
              this.showMessage('Task updated but failed to update some dependencies', 'warning');
              this.loading = false;
            }
          });
        } else {
          this.showMessage('Task updated successfully', 'success');
          this.taskSaved.emit({ id: taskId } as Task);
          this.loading = false;
        }
      },
      error: (error) => {
        console.error('Error loading current dependencies:', error);
        this.showMessage('Task updated but failed to update dependencies', 'warning');
        this.loading = false;
      }
    });
  }

  /**
   * Cancel form
   */
  onCancel(): void {
    this.taskForm.reset();
    this.cancelled.emit();
  }

  /**
   * Mark all form fields as touched
   */
  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
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
   * Get form field error message
   */
  getErrorMessage(fieldName: string): string {
    const control = this.taskForm.get(fieldName);
    if (!control || !control.errors || !control.touched) return '';

    if (control.errors['required']) return `${fieldName} is required`;
    if (control.errors['minlength']) return `${fieldName} must be at least ${control.errors['minlength'].requiredLength} characters`;
    if (control.errors['maxlength']) return `${fieldName} must not exceed ${control.errors['maxlength'].requiredLength} characters`;
    if (control.errors['min']) return `${fieldName} must be at least ${control.errors['min'].min}`;
    if (control.errors['max']) return `${fieldName} must not exceed ${control.errors['max'].max}`;

    return '';
  }

  /**
   * Check if form field has error
   */
  hasError(fieldName: string): boolean {
    const control = this.taskForm.get(fieldName);
    return !!(control && control.invalid && control.touched);
  }

  /**
   * Get task name by ID
   */
  getTaskName(taskId: number): string {
    const task = this.availableTasks.find(t => t.id === taskId);
    return task ? task.name : `Task ${taskId}`;
  }
}

// Made with Bob
