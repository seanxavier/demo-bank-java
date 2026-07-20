import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { ProjectFormComponent } from './components/project-form/project-form.component';
import { TaskFormComponent } from './components/task-form/task-form.component';
import { ScheduleViewComponent } from './components/schedule-view/schedule-view.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    MatTabsModule,
    MatToolbarModule,
    MatIconModule,
    ProjectFormComponent,
    TaskFormComponent,
    ScheduleViewComponent
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'Project Scheduler';
}

// Made with Bob
