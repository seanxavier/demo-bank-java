import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Project } from '../../models/project.model';
import { ProjectService } from '../../services/project.service';

/**
 * Project Form Component
 * Create and edit projects with validation
 */
@Component({
  selector: 'app-project-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatCardModule,
    MatSnackBarModule
  ],
  templateUrl: './project-form.component.html',
  styleUrl: './project-form.component.scss'
})
export class ProjectFormComponent implements OnInit {
  @Input() project?: Project;
  @Input() mode: 'create' | 'edit' = 'create';
  @Output() projectSaved = new EventEmitter<Project>();
  @Output() cancelled = new EventEmitter<void>();

  projectForm!: FormGroup;
  loading = false;
  minDate = new Date(); // No past dates allowed

  constructor(
    private fb: FormBuilder,
    private projectService: ProjectService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initForm();
    if (this.project && this.mode === 'edit') {
      this.loadProject();
    }
  }

  /**
   * Initialize form with validators
   */
  private initForm(): void {
    this.projectForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      startDate: ['', Validators.required],
      description: ['', Validators.maxLength(500)]
    });
  }

  /**
   * Load project data for editing
   */
  private loadProject(): void {
    if (this.project) {
      this.projectForm.patchValue({
        name: this.project.name,
        startDate: new Date(this.project.startDate),
        description: this.project.description || ''
      });
    }
  }

  /**
   * Check if project name already exists
   */
  async checkNameExists(): Promise<void> {
    const name = this.projectForm.get('name')?.value;
    if (!name || name.length < 3) return;

    try {
      const excludeId = this.project?.id;
      const exists = await this.projectService.checkProjectNameExists(name, excludeId).toPromise();
      
      if (exists) {
        this.projectForm.get('name')?.setErrors({ nameExists: true });
        this.showMessage('Project name already exists', 'warning');
      }
    } catch (error) {
      console.error('Error checking project name:', error);
    }
  }

  /**
   * Submit form
   */
  onSubmit(): void {
    if (this.projectForm.invalid) {
      this.markFormGroupTouched(this.projectForm);
      this.showMessage('Please fix form errors', 'error');
      return;
    }

    this.loading = true;
    const formValue = this.projectForm.value;
    
    // Format date to ISO string
    const projectData: Project = {
      name: formValue.name,
      startDate: this.formatDate(formValue.startDate),
      description: formValue.description || undefined
    };

    if (this.mode === 'edit' && this.project?.id) {
      this.updateProject(this.project.id, projectData);
    } else {
      this.createProject(projectData);
    }
  }

  /**
   * Create new project
   */
  private createProject(project: Project): void {
    this.projectService.createProject(project).subscribe({
      next: (created) => {
        this.showMessage('Project created successfully', 'success');
        this.projectSaved.emit(created);
        this.projectForm.reset();
      },
      error: (error) => {
        console.error('Error creating project:', error);
        this.showMessage(error.error?.message || 'Failed to create project', 'error');
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  /**
   * Update existing project
   */
  private updateProject(id: number, project: Project): void {
    this.projectService.updateProject(id, project).subscribe({
      next: (updated) => {
        this.showMessage('Project updated successfully', 'success');
        this.projectSaved.emit(updated);
      },
      error: (error) => {
        console.error('Error updating project:', error);
        this.showMessage(error.error?.message || 'Failed to update project', 'error');
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  /**
   * Cancel form
   */
  onCancel(): void {
    this.projectForm.reset();
    this.cancelled.emit();
  }

  /**
   * Format date to YYYY-MM-DD
   */
  private formatDate(date: Date): string {
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  /**
   * Mark all form fields as touched to show validation errors
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
    const control = this.projectForm.get(fieldName);
    if (!control || !control.errors || !control.touched) return '';

    if (control.errors['required']) return `${fieldName} is required`;
    if (control.errors['minlength']) return `${fieldName} must be at least ${control.errors['minlength'].requiredLength} characters`;
    if (control.errors['maxlength']) return `${fieldName} must not exceed ${control.errors['maxlength'].requiredLength} characters`;
    if (control.errors['nameExists']) return 'Project name already exists';

    return '';
  }

  /**
   * Check if form field has error
   */
  hasError(fieldName: string): boolean {
    const control = this.projectForm.get(fieldName);
    return !!(control && control.invalid && control.touched);
  }
}

// Made with Bob
