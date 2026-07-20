import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Project } from '../models/project.model';
import { environment } from '../../environments/environment';

/**
 * Service for managing projects
 * Communicates with Spring Boot backend API
 */
@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private apiUrl = `${environment.apiUrl}/projects`;

  constructor(private http: HttpClient) {}

  /**
   * Get all projects
   */
  getAllProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(this.apiUrl);
  }

  /**
   * Get project by ID
   */
  getProjectById(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/${id}`);
  }

  /**
   * Get project with tasks
   */
  getProjectWithTasks(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/${id}/with-tasks`);
  }

  /**
   * Create new project
   */
  createProject(project: Project): Observable<Project> {
    return this.http.post<Project>(this.apiUrl, project);
  }

  /**
   * Update existing project
   */
  updateProject(id: number, project: Project): Observable<Project> {
    return this.http.put<Project>(`${this.apiUrl}/${id}`, project);
  }

  /**
   * Delete project
   */
  deleteProject(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Search projects by name
   */
  searchProjects(name: string): Observable<Project[]> {
    const params = new HttpParams().set('name', name);
    return this.http.get<Project[]>(`${this.apiUrl}/search`, { params });
  }

  /**
   * Get projects by date range
   */
  getProjectsByDateRange(startDate: string, endDate: string): Observable<Project[]> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get<Project[]>(`${this.apiUrl}/by-date-range`, { params });
  }

  /**
   * Check if project name exists
   */
  checkProjectNameExists(name: string, excludeId?: number): Observable<boolean> {
    let params = new HttpParams().set('name', name);
    if (excludeId) {
      params = params.set('excludeId', excludeId.toString());
    }
    return this.http.get<boolean>(`${this.apiUrl}/exists`, { params });
  }
}

// Made with Bob
