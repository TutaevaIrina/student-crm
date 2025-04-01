# Student CRM System

## Overview
The **Student CRM System** is a full-stack web application for managing student and course data.  
The solution consists of:
- **Backend Service** (`student-crm-service`) in Kotlin with Spring Boot
- **Frontend** (`student-crm-ui`) using Next.js and TypeScript

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
                ├── course/           # Course-related logic (API, Business, Persistence)
                ├── student/          # Student-related logic (API, Business, Persistence)
                ├── shared/           # Error handling, filters, data types
                └── config/           # Web configuration
 └── test/                            # Unit and integration tests
 └── resources/                       # application.yml, Flyway migrations, etc.
```

### `student-crm-ui/`
```
src/
 └── app/
      ├── components/     # UI components for students & courses
      ├── contexts/       # React contexts for state management
      ├── courses/        # Course pages (routing)
      ├── students/       # Student pages (routing)
      ├── styles/         # Theme & styling
      └── types/          # TypeScript type definitions
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

### Students
- `POST /students` – Add a new student
- `GET /students/{id}` – Get student details

### Courses
- `POST /courses` – Add a new course
- `GET /courses/{id}` – Get course details

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
