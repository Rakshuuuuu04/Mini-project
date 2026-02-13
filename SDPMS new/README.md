# Student Digital Process Monitoring System (SDPMS)

A full-stack web application that digitally tracks, monitors, and manages student academic records: attendance, internal marks, semester marks, grades, and performance reports.

## Complete project contents

| Part | Location | Description |
|------|----------|-------------|
| **Frontend** | `frontend/` | HTML, CSS, JavaScript – login and role-based dashboard |
| **Backend** | `backend/` | Spring Boot REST API (Java 17) |
| **Database** | `database/` | MySQL schema (`schema.sql`) and optional seed data (`seed.sql`) |

**Full setup instructions:** see **[PROJECT_SETUP.md](PROJECT_SETUP.md)** for step-by-step database, backend, and frontend setup.

## Architecture

- **Frontend:** HTML, CSS, JavaScript (convertible to React later)
- **Backend:** Java 17, Spring Boot 3.2 (REST APIs)
- **Database:** MySQL
- **Auth:** JWT + role-based access (Admin, Faculty, Student)

## User Types

| Role    | Capabilities |
|---------|----------------|
| **Admin**  | Manage students, faculty, departments, subjects; view department-wise reports |
| **Faculty**| Mark attendance, enter internal/semester marks, view class-wise reports, low attendance alerts |
| **Student**| View attendance %, marks & grades, progress dashboard, download progress report, receive alerts |

## Quick Start

### Prerequisites

- Java 17+
- Maven
- MySQL 8+ (running on `localhost:3306`)
- (Optional) Live Server or any static file server for frontend

### 1. Database

Create database (optional; Boot can create it):

```sql
CREATE DATABASE IF NOT EXISTS sdpms;
```

Update `backend/src/main/resources/application.properties` if your MySQL username/password differ from `root`/`root`.

### 2. Backend

```bash
cd backend
mvn spring-boot:run
```

Server runs at **http://localhost:8080**. Context path: `/api`.

Default admin login: **username:** `admin`, **password:** `admin123`.

### 3. Frontend

Serve the `frontend` folder (e.g. with Live Server on port 5500, or):

```bash
cd frontend
npx serve -p 5500
```

Open **http://localhost:5500** (or your server URL). Log in with `admin` / `admin123`.

### CORS

Backend allows origins: `http://localhost:5500`, `http://127.0.0.1:5500`, `http://localhost:3000`, `http://127.0.0.1:3000`. Adjust in `SecurityConfig.java` if you use another port.

## API Overview

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Login (returns JWT) |
| GET  | `/api/admin/departments` | List departments (Admin) |
| GET  | `/api/admin/students` | List students (Admin) |
| GET  | `/api/admin/faculty` | List faculty (Admin) |
| GET  | `/api/student/progress` | Student progress: attendance %, marks, SGPA, alerts (Student) |
| GET  | `/api/student/alerts` | Student notifications (Student) |
| POST | `/api/faculty/attendance` | Mark single attendance (Faculty) |
| POST | `/api/faculty/marks` | Save marks (Faculty) |

All authenticated endpoints require header: `Authorization: Bearer <token>`.

## Project Structure

```
SDPMS new/
├── backend/                 # Spring Boot
│   └── src/main/java/com/sdpms/
│       ├── config/          # Security, CORS, DataLoader
│       ├── controller/      # Auth, Admin, Faculty, Student
│       ├── dto/
│       ├── exception/
│       ├── model/           # User, Student, Faculty, Department, Subject, Attendance, StudentMarks, StudentAlert
│       ├── repository/
│       ├── security/        # JWT filter & util
│       └── service/
├── frontend/
│   ├── css/style.css
│   ├── js/api.js, login.js, dashboard.js
│   ├── index.html           # Login
│   └── dashboard.html      # Role-based dashboard (Student sees My Progress)
├── database/schema.sql
└── README.md
```

## Student Dashboard (My Progress)

Students see:

- **Attendance %** (overall for current semester; &lt; 75% highlighted)
- **SGPA** for current semester
- **Marks & grades** per subject (internal, semester, total, grade, grade point)
- **Recent alerts** (e.g. low attendance, poor performance)

## Security

- Passwords stored with **BCrypt**
- **JWT** for stateless auth
- Role-based access with `@PreAuthorize("hasRole('ADMIN')")` etc.

## Future Enhancements

- Parent login
- Mobile app
- SMS/Email notifications
- Placement tracking
- Downloadable PDF progress report
