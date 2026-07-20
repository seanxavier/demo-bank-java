import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TaskDependency } from '../models/project.model';
import { environment } from '../../environments/environment';

/**
 * Service for managing task dependencies
 * Communicates with Spring Boot backend API
 */
@Injectable({
  providedIn: 'root'
})
export class DependencyService {
  private apiUrl = `${environment.apiUrl}/dependencies`;

  constructor(private http: HttpClient) {}

  /**
   * Get all dependencies
   */
  getAllDependencies(): Observable<TaskDependency[]> {
    return this.http.get<TaskDependency[]>(this.apiUrl);
  }

  /**
   * Get dependency by ID
   */
  getDependencyById(id: number): Observable<TaskDependency> {
    return this.http.get<TaskDependency>(`${this.apiUrl}/${id}`);
  }

  /**
   * Get dependencies for a task
   */
  getDependenciesForTask(taskId: number): Observable<TaskDependency[]> {
    return this.http.get<TaskDependency[]>(`${this.apiUrl}/task/${taskId}`);
  }

  /**
   * Get tasks that depend on a specific task
   */
  getDependentTasks(taskId: number): Observable<TaskDependency[]> {
    return this.http.get<TaskDependency[]>(`${this.apiUrl}/depends-on/${taskId}`);
  }

  /**
   * Get all dependencies for a project
   */
  getDependenciesByProject(projectId: number): Observable<TaskDependency[]> {
    return this.http.get<TaskDependency[]>(`${this.apiUrl}/project/${projectId}`);
  }

  /**
   * Create new dependency
   */
  createDependency(dependency: TaskDependency): Observable<TaskDependency> {
    return this.http.post<TaskDependency>(this.apiUrl, dependency);
  }

  /**
   * Delete dependency
   */
  deleteDependency(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Delete dependency by task IDs
   */
  deleteDependencyByTaskIds(taskId: number, dependsOnTaskId: number): Observable<void> {
    const params = new HttpParams()
      .set('taskId', taskId.toString())
      .set('dependsOnTaskId', dependsOnTaskId.toString());
    return this.http.delete<void>(`${this.apiUrl}/by-tasks`, { params });
  }

  /**
   * Check if dependency exists
   */
  dependencyExists(taskId: number, dependsOnTaskId: number): Observable<boolean> {
    const params = new HttpParams()
      .set('taskId', taskId.toString())
      .set('dependsOnTaskId', dependsOnTaskId.toString());
    return this.http.get<boolean>(`${this.apiUrl}/exists`, { params });
  }

  /**
   * Validate no circular dependency
   * Returns true if valid (no circular dependency)
   */
  validateNoCircularDependency(taskId: number, dependsOnTaskId: number): Observable<boolean> {
    const params = new HttpParams()
      .set('taskId', taskId.toString())
      .set('dependsOnTaskId', dependsOnTaskId.toString());
    return this.http.get<boolean>(`${this.apiUrl}/validate-no-circular`, { params });
  }

  /**
   * Check for circular dependencies in project
   * Returns list of task IDs involved in circular dependencies
   */
  checkCircularDependencies(projectId: number): Observable<number[]> {
    return this.http.get<number[]>(`${this.apiUrl}/project/${projectId}/circular-check`);
  }
}

// Made with Bob
