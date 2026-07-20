# Project Scheduler - Backend

A Spring Boot REST API for project scheduling with ASAP (As Soon As Possible) algorithm, circular dependency detection, and comprehensive audit trail using AOP.

## 🚀 Features

- **ASAP Scheduling Algorithm**: Automatically schedules tasks to start as soon as dependencies complete
- **Circular Dependency Detection**: DFS-based algorithm prevents invalid dependency chains
- **Story Points Validation**: Fibonacci sequence validation (1, 2, 3, 5, 8, 13, 21, 34, 55, 89)
- **AOP Logging & Audit Trail**: Automatic logging and audit tracking for all operations
- **RESTful API**: Complete CRUD operations for projects, tasks, and dependencies
- **H2 In-Memory Database**: Easy setup and testing
- **Exception Handling**: Comprehensive error handling with meaningful messages

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+ or Gradle 7+
- IDE (IntelliJ IDEA, Eclipse, or VS Code with Java extensions)

## 🛠️ Technology Stack

- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring AOP**
- **H2 Database**
- **Lombok**
- **SLF4J + Logback**
- **Jakarta Validation**

## 📦 Installation

### 1. Clone the repository

```bash
cd project-scheduler-backend
```

### 2. Build the project

Using Maven:
```bash
mvn clean install
```

Using Gradle:
```bash
gradle clean build
```

### 3. Run the application

Using Maven:
```bash
mvn spring-boot:run
```

Using Gradle:
```bash
gradle bootRun
```

Using JAR:
```bash
java -jar target/project-scheduler-backend-1.0.0.jar
```

The application will start on **http://localhost:8080**

## 🗄️ Database

The application uses H2 in-memory database. You can access the H2 console at:

**URL**: http://localhost:8080/h2-console

**Connection Details**:
- JDBC URL: `jdbc:h2:mem:schedulerdb`
- Username: `sa`
- Password: (leave empty)

## 📡 API Endpoints

### Projects

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/projects` | Create a new project |
| GET | `/api/projects` | Get all projects |
| GET | `/api/projects/{id}` | Get project by ID |
| GET | `/api/projects/{id}/with-tasks` | Get project with all tasks |
| GET | `/api/projects/search?name={name}` | Search projects by name |
| PUT | `/api/projects/{id}` | Update project |
| DELETE | `/api/projects/{id}` | Delete project |

### Tasks

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/tasks` | Create a new task |
| GET | `/api/tasks` | Get all tasks |
| GET | `/api/tasks/{id}` | Get task by ID |
| GET | `/api/tasks/{id}/with-dependencies` | Get task with dependencies |
| GET | `/api/tasks/project/{projectId}` | Get tasks by project |
| GET | `/api/tasks/status/{status}` | Get tasks by status |
| GET | `/api/tasks/valid-story-points` | Get valid Fibonacci numbers |
| PUT | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Delete task |

### Task Dependencies

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/dependencies` | Create a new dependency |
| GET | `/api/dependencies/{id}` | Get dependency by ID |
| GET | `/api/dependencies/task/{taskId}` | Get dependencies for a task |
| GET | `/api/dependencies/project/{projectId}` | Get all dependencies for a project |
| POST | `/api/dependencies/validate` | Validate dependency (check for circular) |
| DELETE | `/api/dependencies/{id}` | Delete dependency |

### Schedule

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/schedule/project/{projectId}` | Generate schedule for a project |
| POST | `/api/schedule/project/{projectId}/regenerate` | Regenerate schedule |

## 📝 API Examples

### Create a Project

```bash
curl -X POST http://localhost:8080/api/projects \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Website Redesign",
    "startDate": "2026-08-01",
    "description": "Complete website redesign project"
  }'
```

### Create a Task

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Design Mockups",
    "daysRequired": 5,
    "storyPoints": 5,
    "projectId": 1
  }'
```

### Create a Dependency

```bash
curl -X POST http://localhost:8080/api/dependencies \
  -H "Content-Type: application/json" \
  -d '{
    "taskId": 2,
    "dependsOnTaskId": 1
  }'
```

### Generate Schedule

```bash
curl http://localhost:8080/api/schedule/project/1
```

## 🏗️ Project Structure

```
src/main/java/com/scheduler/
├── ProjectSchedulerApplication.java    # Main application class
├── model/                              # JPA entities
│   ├── Project.java
│   ├── Task.java
│   ├── TaskDependency.java
│   └── AuditLog.java
├── repository/                         # Spring Data repositories
│   ├── ProjectRepository.java
│   ├── TaskRepository.java
│   ├── TaskDependencyRepository.java
│   └── AuditLogRepository.java
├── service/                            # Business logic
│   ├── ProjectService.java
│   ├── TaskService.java
│   ├── TaskDependencyService.java
│   └── SchedulingService.java          # Core scheduling algorithm
├── controller/                         # REST controllers
│   ├── ProjectController.java
│   ├── TaskController.java
│   ├── TaskDependencyController.java
│   └── ScheduleController.java
├── dto/                                # Data Transfer Objects
│   ├── ProjectDTO.java
│   ├── TaskDTO.java
│   ├── TaskDependencyDTO.java
│   └── ScheduleDTO.java
├── exception/                          # Exception handling
│   ├── CircularDependencyException.java
│   ├── ValidationException.java
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java
├── aspect/                             # AOP aspects
│   ├── LoggingAspect.java
│   └── AuditAspect.java
├── annotation/                         # Custom annotations
│   ├── Loggable.java
│   └── Auditable.java
└── config/                             # Configuration
    └── CorsConfig.java
```

## 🔍 Core Algorithms

### ASAP Scheduling Algorithm

1. **Topological Sort**: Orders tasks using Kahn's algorithm
2. **Circular Dependency Detection**: Uses DFS to detect cycles
3. **Date Calculation**:
   - Tasks with no dependencies start on project start date
   - Dependent tasks start the day after their latest dependency ends
   - End date = start date + days required - 1

### Story Points Validation

Valid story points must be Fibonacci numbers:
- **Allowed**: 1, 2, 3, 5, 8, 13, 21, 34, 55, 89
- **Not Allowed**: 4, 6, 7, 9, 10, etc.

## 🔐 AOP Features

### @Loggable Annotation
Automatically logs:
- Method entry/exit
- Parameters and return values
- Execution time
- Exceptions

### @Auditable Annotation
Automatically tracks:
- Entity type and action (CREATE, UPDATE, DELETE)
- Before/after state
- Timestamp and user
- Changes made

## ⚙️ Configuration

Edit `src/main/resources/application.properties`:

```properties
# Server port
server.port=8080

# H2 Database
spring.datasource.url=jdbc:h2:mem:schedulerdb
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Logging
logging.level.com.scheduler=DEBUG

# CORS
cors.allowed-origins=http://localhost:4200
```

## 🧪 Testing

Run tests:
```bash
mvn test
```

## 📊 Sample Workflow

1. **Create a Project**
   ```
   POST /api/projects
   ```

2. **Add Tasks**
   ```
   POST /api/tasks (multiple times)
   ```

3. **Define Dependencies**
   ```
   POST /api/dependencies (for each dependency)
   ```

4. **Generate Schedule**
   ```
   GET /api/schedule/project/{projectId}
   ```

5. **View Results**
   - Schedule with calculated start/end dates
   - Tasks ordered by dependencies
   - Total project duration

## 🐛 Troubleshooting

### Port Already in Use
Change the port in `application.properties`:
```properties
server.port=8081
```

### Database Connection Issues
Verify H2 console settings and JDBC URL

### Circular Dependency Error
Check task dependencies - ensure no cycles exist

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring AOP](https://docs.spring.io/spring-framework/reference/core/aop.html)

## 👥 Authors

Built with Spring Boot 3.2.0, Java 17, and AOP

## 📄 License

This project is for demonstration purposes.