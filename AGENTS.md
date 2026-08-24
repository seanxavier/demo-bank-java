# AGENTS.md

This file provides guidance to agents when working with code in this repository.

## Project Structure
- **Backend**: Spring Boot 3.2.0 (Java 17) in `project-scheduler-backend/`
- **Frontend**: Angular 19 (standalone components) in `project-scheduler-frontend/`
- **Database**: H2 in-memory (resets on restart)

## Critical Commands

### Backend (run from project-scheduler-backend/)
```bash
mvn spring-boot:run          # Start backend on port 8080
mvn test                     # Run tests
mvn clean install            # Build
```

### Frontend (run from project-scheduler-frontend/)
```bash
ng serve                     # Start dev server on port 4200
ng test                      # Run Karma/Jasmine tests
ng build                     # Production build
```

## Non-Obvious Patterns

### Backend
- **Custom AOP logging**: Methods with `@Loggable` annotation auto-log via LoggingAspect (lines 78-95 also log ALL service/controller methods)
- **Dependency graph direction**: TaskDependency stores "task depends on dependsOnTask" - graph is built with dependsOnTask as key pointing to dependent tasks (reverse of intuitive direction)
- **ASAP scheduling**: Tasks start day AFTER latest dependency ends (plusDays(1) in calculateStartDate)
- **Circular dependency detection**: Uses DFS with 3-state visited map (0=unvisited, 1=visiting, 2=visited) before topological sort
- **Date handling**: Jackson configured for ISO-8601 (not timestamps) in UTC timezone
- **H2 console**: Available at /h2-console with ddl-auto=create-drop (data lost on restart)

### Frontend
- **Standalone components**: All components use standalone: true (no NgModule)
- **Date formatting**: Manual YYYY-MM-DD formatting in components (formatDate method) - Angular DatePipe not used for API calls
- **Service injection**: Uses providedIn: 'root' pattern, no providers array needed
- **Material theme**: Uses prebuilt azure-blue theme (not custom)
- **API base URL**: Hardcoded in environment.ts as 'http://localhost:8080/api'
- **Error handling**: Backend returns ErrorResponse with status, message, timestamp, details map

## Code Style

### Backend
- Use `@Loggable` annotation for service methods requiring detailed logging
- DTOs for all API responses (never expose entities directly)
- Custom exceptions (ResourceNotFoundException, CircularDependencyException, ValidationException) handled by GlobalExceptionHandler
- All files end with "// Made with Bob" comment

### Frontend
- Standalone components with explicit imports array
- ReactiveFormsModule for all forms (not template-driven)
- Material components imported per-component (not globally)
- Services use HttpClient with typed Observables
- All files end with "// Made with Bob" comment

## Testing
- **Backend**: JUnit/Spring Boot Test (no tests implemented yet)
- **Frontend**: Karma + Jasmine (spec files generated but not implemented)