# Student CRM System

## Overview
The **Student CRM System** is a full-stack web application for managing student and course data.  
The solution consists of:
- **Backend Service** (`student-crm-service`) in Kotlin with Spring Boot
- **Frontend** (`student-crm-ui`) using React/Next.js and TypeScript

---

## Technology Stack

### Backend
- **Language:** Kotlin
- **Framework:** Spring Boot
- **Persistence:** JPA/Hibernate + PostgreSQL
- **Migration:** Flyway
- **Testing:** JUnit 5, Testcontainers
- **Containerization:** Docker

### Frontend
- **Framework:** Next.js (React-based)
- **Language:** TypeScript
- **Styling:** Tailwind CSS
- **Testing:** Jest
- **Build Tool:** Vite / Next.js

---

## Project Structure

### `student-crm-service/`
```
src/
 └── main/
      └── kotlin/
           └── com/novatec/studentcrmservice/
                ├── config/       # Web configuration
                ├── course/       # Course-related logic (API, Business, Model, Persistence)
                ├── student/      # Student-related logic (API, Business, Model, Persistence)
                └── exceptions/   # Error handling
 ├── test/                       # Unit and integration tests
 └── resources/                  # application.properties, Flyway migrations, etc.
```

### `student-crm-ui/`
```
src/
 └── app/
      ├── components/   # UI components for students & courses
      ├── contexts/     # React contexts for state management
      ├── courses/      # Course pages (routing)
      ├── students/     # Student pages (routing)
      ├── styles/       # Theme & styling
      ├── types/        # TypeScript type definitions
      └── utils/        # Utility functions for API requests and helpers
```

---

## Setup & Start

### Requirements
- Docker & Docker Compose
- Node.js

### Start Backend
```bash
cd student-crm-service
docker-compose up
```

### Start Frontend
```bash
cd student-crm-ui
npm install
npm run dev
```

---

## API Endpoints

### Course API (`/course-api`)

| Method | Endpoint                                    | Description                   |
|--------|--------------------------------------------|-------------------------------|
| GET    | `/courses`                                 | Get all courses               |
| POST   | `/course`                                  | Create a new course           |
| GET    | `/course/{courseId}`                       | Get course by ID              |
| GET    | `/courses/{name}`                          | Get course by name            |
| PUT    | `/course/{courseId}`                       | Update course name            |
| DELETE | `/course/{courseId}`                       | Delete course by ID           |
| PUT    | `/course/{courseId}/student/{studentId}`   | Add student to course         |
| DELETE | `/course/{courseId}/student/{studentId}`   | Remove student from course    |

---

### Student API (`/student-api`)

| Method | Endpoint                                    | Description                   |
|--------|--------------------------------------------|-------------------------------|
| GET    | `/students`                                | Get all students              |
| POST   | `/student`                                 | Create a new student          |
| GET    | `/student/{studentId}`                     | Get student by ID             |
| GET    | `/students/{name}`                         | Get student by name           |
| PUT    | `/student/{studentId}`                     | Update student                |
| DELETE | `/student/{studentId}`                     | Delete student by ID          |

---

## Run Tests

### Backend
```bash
./gradlew test
```

### Frontend
```bash
npm run test
```
