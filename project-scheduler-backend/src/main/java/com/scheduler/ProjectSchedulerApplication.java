package com.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Main Spring Boot application class for Project Scheduler.
 * Enables AOP for logging and audit trail functionality.
 */
@SpringBootApplication
@EnableAspectJAutoProxy
public class ProjectSchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectSchedulerApplication.class, args);
    }
}

// Made with Bob
