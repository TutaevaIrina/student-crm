# Student CRM Service

## Overview
The "Student CRM Service" project is a Kotlin-based backend system designed to manage student and course data at educational institutions. It provides APIs for creating, updating, and querying student and course information.

## Technology Stack
- **Language**: Kotlin
- **Framework**: Spring Boot
- **Database**: PostgreSQL
- **Other Technologies**:
    - Docker for containerization
    - Flyway for database migrations
    - JUnit for testing

## Project Structure
- `src/main/kotlin/`: Contains the main project code.
    - `api/`: Controller classes for handling API requests.
    - `business/`: Service classes with business logic.
    - `model/`: DTOs and entity classes.
    - `persistence/`: Repositories for data access.
- `src/test/kotlin/`: Contains test classes.
- `resources/`: Configuration files and resources.
- `db/migration/`: SQL scripts for database migrations.

## Setup and Installation
Ensure Docker is installed on your system to deploy the environment.

1. Clone the repository.
2. Start the application using Docker Compose: docker-compose up

## API Endpoints
- `POST /students`: Adds a new student.
- `GET /students/{id}`: Retrieves details of a student.
- `POST /courses`: Adds a new course.
- `GET /courses/{id}`: Retrieves details of a course.