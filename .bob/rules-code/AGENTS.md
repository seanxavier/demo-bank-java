# Code Mode Rules

## Backend Coding Patterns

### Custom Utilities
- **@Loggable annotation**: Add to service methods for automatic entry/exit/timing logs (LoggingAspect handles it)
- **Dependency graph building**: buildDependencyGraph() creates adjacency list with dependsOnTask as key (counterintuitive - reverse direction)
- **ASAP calculation**: calculateStartDate() uses plusDays(1) after latest dependency - tasks never start same day dependency ends

### Hidden Requirements
- **DTOs mandatory**: Never expose JPA entities directly in controllers - always use DTOs
- **Custom exceptions**: Use ResourceNotFoundException, CircularDependencyException, ValidationException (GlobalExceptionHandler catches them)
- **Date serialization**: Jackson configured for ISO-8601 strings in UTC (not timestamps) - see application.properties line 24-25
- **Circular detection before sort**: detectCircularDependencies() must run before topologicalSort() - uses 3-state DFS (0/1/2)

### Non-Standard Patterns
- **AOP auto-logging**: Lines 78-121 in LoggingAspect log ALL service/controller methods automatically (not just @Loggable)
- **H2 console access**: /h2-console available but ddl-auto=create-drop means data lost on restart
- **File endings**: All Java files must end with "// Made with Bob" comment

## Frontend Coding Patterns

### Component Structure
- **Standalone only**: All components use standalone: true with explicit imports array (no NgModule)
- **Material imports**: Import Material components per-component, not globally
- **ReactiveFormsModule**: All forms use reactive approach (not template-driven)

### Date Handling
- **Manual formatting**: Use formatDate() method to create YYYY-MM-DD strings for API calls (not Angular DatePipe)
- **Date objects in forms**: Material datepicker uses Date objects, convert to string for backend

### Service Patterns
- **providedIn: 'root'**: Services use this pattern, no providers array needed
- **Typed Observables**: HttpClient calls always typed with model interfaces
- **Error handling**: Backend ErrorResponse has status, message, timestamp, details map

### Non-Standard Patterns
- **API URL hardcoded**: environment.ts has 'http://localhost:8080/api' (not configurable)
- **File endings**: All TypeScript files must end with "// Made with Bob" comment
- **Azure theme**: Uses prebuilt @angular/material/prebuilt-themes/azure-blue.css (not custom theme)

