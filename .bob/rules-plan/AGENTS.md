# Plan Mode Architecture Rules

## Core Architecture Constraints

### Backend Architecture
- **Layered architecture**: Controller → Service → Repository pattern strictly enforced
- **DTO layer mandatory**: Entities never exposed directly to controllers
- **AOP cross-cutting**: LoggingAspect and AuditAspect intercept all service/controller methods automatically
- **Exception handling**: GlobalExceptionHandler centralizes all error responses with structured ErrorResponse

### Frontend Architecture
- **Standalone components**: No NgModule pattern - all components standalone with explicit imports
- **Service layer**: All HTTP calls go through typed service layer (never direct HttpClient in components)
- **Reactive forms**: FormBuilder pattern used throughout (no template-driven forms)
- **Material Design**: Components import Material modules individually (not app-wide)

## Hidden Coupling & Dependencies

### Backend
- **Dependency graph direction**: TaskDependency stores "task depends on dependsOnTask" but buildDependencyGraph() uses dependsOnTask as key pointing to dependent tasks (reverse direction)
- **Scheduling order**: detectCircularDependencies() MUST run before topologicalSort() - not optional
- **Date calculations**: ASAP algorithm uses plusDays(1) after dependency ends - tasks never start same day
- **Transaction boundaries**: @Transactional on service methods, not controllers

### Frontend
- **Date conversion**: Components convert Date objects to YYYY-MM-DD strings manually before API calls
- **Error structure**: All error handling expects backend ErrorResponse format (status, message, timestamp, details)
- **Observable pattern**: Services return Observables, components subscribe (no Promises)

## Non-Standard Architectural Decisions

### Backend
- **H2 in-memory only**: Database resets on restart (ddl-auto=create-drop) - not production-ready
- **Circular detection algorithm**: Uses 3-state DFS (0/1/2) instead of standard 2-state
- **Logging everywhere**: LoggingAspect logs ALL service/controller methods (lines 78-121) regardless of @Loggable
- **Jackson date config**: ISO-8601 strings in UTC (not timestamps) - see application.properties line 24-25

### Frontend
- **No routing**: App uses single-page component switching (no Angular Router configured)
- **Hardcoded API URL**: environment.ts has fixed 'http://localhost:8080/api' (not configurable per environment)
- **Material theme**: Uses prebuilt azure-blue.css (not custom theme with variables)

## Performance Considerations
- **H2 in-memory**: Fast but data lost on restart - not suitable for production
- **Topological sort**: O(V+E) complexity for task scheduling
- **AOP overhead**: All service/controller methods logged - consider disabling in production

## Testing Architecture
- **Backend**: JUnit/Spring Boot Test framework configured but no tests implemented
- **Frontend**: Karma + Jasmine configured but spec files empty (not implemented)