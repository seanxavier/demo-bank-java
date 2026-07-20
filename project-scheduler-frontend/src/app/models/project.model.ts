/**
 * Project model matching backend ProjectDTO
 */
export interface Project {
  id?: number;
  name: string;
  startDate: string; // ISO 8601 date string
  description?: string;
  tasks?: Task[];
  createdAt?: string;
  updatedAt?: string;
}

/**
 * Task model matching backend TaskDTO
 */
export interface Task {
  id?: number;
  name: string;
  daysRequired: number;
  storyPoints: number;
  projectId: number;
  status?: TaskStatus;
  scheduledStartDate?: string;
  scheduledEndDate?: string;
  dependencies?: number[]; // Array of task IDs
  createdAt?: string;
  updatedAt?: string;
}

/**
 * Task status enum
 */
export enum TaskStatus {
  PENDING = 'PENDING',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  BLOCKED = 'BLOCKED'
}

/**
 * Task dependency model matching backend TaskDependencyDTO
 */
export interface TaskDependency {
  id?: number;
  taskId: number;
  dependsOnTaskId: number;
  taskName?: string;
  dependsOnTaskName?: string;
}

/**
 * Schedule model matching backend ScheduleDTO
 */
export interface Schedule {
  projectId: number;
  projectName: string;
  projectStartDate: string;
  tasks: ScheduledTask[];
  totalDuration: number;
  generatedAt: string;
}

/**
 * Scheduled task model
 */
export interface ScheduledTask {
  taskId: number;
  taskName: string;
  daysRequired: number;
  storyPoints: number;
  scheduledStartDate: string;
  scheduledEndDate: string;
  dependencies: string[]; // Array of dependency task names
  status: TaskStatus;
}

/**
 * Valid Fibonacci story points
 */
export const VALID_STORY_POINTS = [1, 2, 3, 5, 8, 13, 21, 34, 55, 89];

/**
 * API error response
 */
export interface ApiError {
  message: string;
  status: number;
  timestamp: string;
  path?: string;
}

// Made with Bob
