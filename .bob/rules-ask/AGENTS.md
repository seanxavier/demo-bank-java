# Ask Mode Documentation Rules

## Project Structure Context

### Backend (project-scheduler-backend/)
- **Spring Boot 3.2.0** with Java 17
- **H2 in-memory database** - data resets on restart (not persistent)
- **AOP logging** - LoggingAspect automatically logs service/controller methods (lines 78-121)
- **Custom annotations**: @Loggable for detailed method logging, @Auditable for audit trails

### Frontend (project-scheduler-frontend/)
- **Angular 19** with standalone components (no NgModule pattern)
- **Material Design** using prebuilt azure-blue theme
- **Reactive forms** throughout (not template-driven)
- **TypeScript strict mode** enabled

## Non-Obvious Documentation Locations

### Backend
- **Scheduling algorithm**: SchedulingService.java contains ASAP (As Soon As Possible) implementation
- **Dependency graph**: Built with dependsOnTask as key (reverse of intuitive direction) - see buildDependencyGraph()
- **Circular detection**: Uses 3-state DFS (0=unvisited, 1=visiting, 2=visited) before topological sort
- **Error responses**: GlobalExceptionHandler.ErrorResponse has status, message, timestamp, details map structure

### Frontend
- **Date formatting**: Components use manual formatDate() method (YYYY-MM-DD) instead of Angular DatePipe for API calls
- **Service pattern**: All services use providedIn: 'root' (no providers array in components)
- **API configuration**: environment.ts hardcodes 'http://localhost:8080/api' (not environment-variable driven)

## Hidden Architectural Decisions

### Backend
- **DTOs required**: Never expose JPA entities directly - always use DTOs in controllers
- **Task scheduling**: Tasks start day AFTER latest dependency ends (plusDays(1) in calculateStartDate)
- **Database**: H2 console at /h2-console but ddl-auto=create-drop means data lost on restart
- **Date handling**: Jackson configured for ISO-8601 strings in UTC (not timestamps)

### Frontend
- **Standalone components**: All use standalone: true with explicit imports array
- **Material imports**: Components import Material modules individually (not globally)
- **Error handling**: Backend returns structured ErrorResponse, not plain strings

## File Conventions
- All Java files end with "// Made with Bob" comment
- All TypeScript files end with "// Made with Bob" comment