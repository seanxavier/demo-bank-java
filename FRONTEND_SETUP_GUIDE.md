# Angular 19+ Frontend Setup Guide

## Prerequisites

Before starting the Angular frontend, you need to install:

1. **Node.js** (v18 or higher)
2. **npm** (comes with Node.js)
3. **Angular CLI** (v19 or higher)

## Installation Steps

### 1. Install Node.js

Download and install Node.js from: https://nodejs.org/

Verify installation:
```bash
node --version  # Should be v18 or higher
npm --version   # Should be 9 or higher
```

### 2. Install Angular CLI 19

```bash
npm install -g @angular/cli@19
```

Verify installation:
```bash
ng version
```

### 3. Create Angular Project

Navigate to the project directory:
```bash
cd /Users/seanxaviernieva/Documents/GitHub/demo-banking-java
```

Create the Angular project:
```bash
ng new project-scheduler-frontend --routing --style=scss --standalone
```

When prompted:
- Would you like to add Angular routing? **Yes**
- Which stylesheet format would you like to use? **SCSS**
- Do you want to enable Server-Side Rendering (SSR)? **No**

### 4. Navigate to Frontend Directory

```bash
cd project-scheduler-frontend
```

### 5. Install Angular Material

```bash
ng add @angular/material
```

When prompted:
- Choose a prebuilt theme: **Indigo/Pink**
- Set up global Angular Material typography styles? **Yes**
- Include the Angular animations module? **Yes**

### 6. Install Additional Dependencies

```bash
npm install --save date-fns
```

### 7. Start Development Server

```bash
ng serve
```

The application will be available at: **http://localhost:4200**

---

## Project Structure (To Be Created)

```
project-scheduler-frontend/
├── src/
│   ├── app/
│   │   ├── models/
│   │   │   ├── project.model.ts
│   │   │   ├── task.model.ts
│   │   │   ├── dependency.model.ts
│   │   │   └── schedule.model.ts
│   │   ├── services/
│   │   │   ├── project.service.ts
│   │   │   ├── task.service.ts
│   │   │   ├── dependency.service.ts
│   │   │   └── schedule.service.ts
│   │   ├── components/
│   │   │   ├── project-form/
│   │   │   ├── task-form/
│   │   │   ├── dependency-manager/
│   │   │   ├── schedule-view/
│   │   │   └── navigation/
│   │   ├── app.component.ts
│   │   ├── app.component.html
│   │   ├── app.component.scss
│   │   ├── app.config.ts
│   │   └── app.routes.ts
│   ├── environments/
│   │   ├── environment.ts
│   │   └── environment.prod.ts
│   ├── index.html
│   ├── main.ts
│   └── styles.scss
├── angular.json
├── package.json
├── tsconfig.json
└── README.md
```

---

## Configuration

### Update environment.ts

Create `src/environments/environment.ts`:
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

Create `src/environments/environment.prod.ts`:
```typescript
export const environment = {
  production: true,
  apiUrl: 'http://localhost:8080/api'
};
```

### Update angular.json

Add environments configuration in `angular.json`:
```json
"configurations": {
  "production": {
    "fileReplacements": [
      {
        "replace": "src/environments/environment.ts",
        "with": "src/environments/environment.prod.ts"
      }
    ]
  }
}
```

---

## Next Steps After Setup

Once Angular CLI is installed and the project is created, we will:

1. **Create Models** (TypeScript interfaces)
   - Project, Task, TaskDependency, Schedule models

2. **Create Services** (HTTP communication)
   - ProjectService, TaskService, DependencyService, ScheduleService
   - Configure HttpClient with interceptors

3. **Create Components**
   - Project form (create/edit projects)
   - Task form (create/edit tasks with story points)
   - Dependency manager (add/remove dependencies)
   - Schedule view (display calculated schedule)
   - Navigation (routing between views)

4. **Configure Routing**
   - /projects - List all projects
   - /projects/new - Create new project
   - /projects/:id - View/edit project
   - /projects/:id/tasks - Manage tasks
   - /projects/:id/schedule - View schedule

5. **Add Material Components**
   - Forms with validation
   - Data tables
   - Date pickers
   - Dialogs
   - Snackbars for notifications

6. **Implement Features**
   - Story points selector (Fibonacci: 1, 2, 3, 5, 8, 13, 21, etc.)
   - Dependency validation (prevent circular dependencies)
   - Schedule generation and display
   - Error handling and user feedback

---

## Running Both Backend and Frontend

### Terminal 1 - Backend
```bash
cd project-scheduler-backend
mvn spring-boot:run
```
Backend runs on: http://localhost:8080

### Terminal 2 - Frontend
```bash
cd project-scheduler-frontend
ng serve
```
Frontend runs on: http://localhost:4200

---

## Troubleshooting

### Port Already in Use

**Backend (8080):**
Change port in `application.properties`:
```properties
server.port=8081
```

**Frontend (4200):**
Run with different port:
```bash
ng serve --port 4201
```

### CORS Issues

Ensure backend CORS is configured for frontend URL in `application.properties`:
```properties
cors.allowed-origins=http://localhost:4200
```

### Angular CLI Not Found

Install globally:
```bash
npm install -g @angular/cli@19
```

Or use npx:
```bash
npx @angular/cli@19 new project-scheduler-frontend
```

---

## Ready to Proceed?

Once you have:
1. ✅ Node.js installed
2. ✅ Angular CLI 19 installed
3. ✅ Angular project created
4. ✅ Angular Material added

We can proceed with creating the frontend components and services!

---

**Current Status:**
- ✅ Backend: 100% Complete
- ⏳ Frontend: Awaiting Angular CLI installation
- 📊 Overall: 58% Complete