import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task, VALID_STORY_POINTS } from '../models/project.model';
import { environment } from '../../environments/environment';

/**
 * Service for managing tasks
 * Communicates with Spring Boot backend API
 */
@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private apiUrl = `${environment.apiUrl}/tasks`;

  constructor(private http: HttpClient) {}

  /**
   * Get all tasks
   */
  getAllTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(this.apiUrl);
  }

  /**
   * Get task by ID
   */
  getTaskById(id: number): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/${id}`);
  }

  /**
   * Get tasks by project ID
   */
  getTasksByProjectId(projectId: number): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/project/${projectId}`);
  }

  /**
   * Get tasks by project ID with dependencies
   */
  getTasksWithDependencies(projectId: number): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/project/${projectId}/with-dependencies`);
  }

  /**
   * Get tasks with no dependencies
   */
  getTasksWithNoDependencies(projectId: number): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/project/${projectId}/no-dependencies`);
  }

  /**
   * Create new task
   */
  createTask(task: Task): Observable<Task> {
    return this.http.post<Task>(this.apiUrl, task);
  }

  /**
   * Update existing task
   */
  updateTask(id: number, task: Task): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/${id}`, task);
  }

  /**
   * Delete task
   */
  deleteTask(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Update task status
   */
  updateTaskStatus(id: number, status: string): Observable<Task> {
    const params = new HttpParams().set('status', status);
    return this.http.patch<Task>(`${this.apiUrl}/${id}/status`, null, { params });
  }

  /**
   * Get valid story points (Fibonacci sequence)
   */
  getValidStoryPoints(): Observable<number[]> {
    return this.http.get<number[]>(`${this.apiUrl}/valid-story-points`);
  }

  /**
   * Validate story points
   */
  validateStoryPoints(storyPoints: number): Observable<boolean> {
    const params = new HttpParams().set('storyPoints', storyPoints.toString());
    return this.http.get<boolean>(`${this.apiUrl}/validate-story-points`, { params });
  }

  /**
   * Get valid story points from local constant
   * (Faster than API call for UI dropdowns)
   */
  getValidStoryPointsLocal(): number[] {
    return VALID_STORY_POINTS;
  }

  /**
   * Validate story points locally
   */
  isValidStoryPoints(storyPoints: number): boolean {
    return VALID_STORY_POINTS.includes(storyPoints);
  }

  /**
   * Search tasks by name
   */
  searchTasks(name: string, projectId?: number): Observable<Task[]> {
    let params = new HttpParams().set('name', name);
    if (projectId) {
      params = params.set('projectId', projectId.toString());
    }
    return this.http.get<Task[]>(`${this.apiUrl}/search`, { params });
  }

  /**
   * Get tasks by status
   */
  getTasksByStatus(status: string, projectId?: number): Observable<Task[]> {
    let params = new HttpParams().set('status', status);
    if (projectId) {
      params = params.set('projectId', projectId.toString());
    }
    return this.http.get<Task[]>(`${this.apiUrl}/by-status`, { params });
  }
}

// Made with Bob
