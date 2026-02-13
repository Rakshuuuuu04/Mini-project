# SDPMS – Complete Project Setup Guide

This guide gets the **frontend**, **backend**, and **database** running on your machine.

---

## Prerequisites

- **Java 17** or higher ([Adoptium](https://adoptium.net/) or Oracle JDK)
- **Maven 3.6+** ([maven.apache.org](https://maven.apache.org/download.cgi))
- **MySQL 8** (or 5.7) installed and running ([dev.mysql.com](https://dev.mysql.com/downloads/))
- A **browser** and a way to serve static files (e.g. VS Code Live Server, or `npx serve`)

---

## 1. Database Setup

### Option A: Let Spring Boot create everything (easiest)

1. Start MySQL.
2. Create a database (optional; Boot can create it):
   ```sql
   CREATE DATABASE IF NOT EXISTS sdpms;
   ```
3. In `backend/src/main/resources/application.properties`, set your MySQL username and password:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```
4. When you run the backend (Step 2), tables will be created automatically (`spring.jpa.hibernate.ddl-auto=update`).

### Option B: Run the SQL scripts manually

1. Start MySQL. Create database and tables:
   ```bash
   mysql -u root -p < database/schema.sql
   ```
2. (Optional) Add sample departments and subjects:
   ```bash
   mysql -u root -p < database/seed.sql
   ```
3. Set `application.properties` as in Option A. When you run the backend, it will use these tables (and still create any missing ones if using `update`).

---

## 2. Backend (Spring Boot)

1. Open a terminal in the project folder.
2. Go to the backend directory:
   ```bash
   cd backend
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
   Or open the project in an IDE and run the main class: `com.sdpms.SdpmsApplication`.

4. Wait until you see something like: `Started SdpmsApplication in ... seconds`.

5. Backend base URL: **http://localhost:8080**  
   - API context path: **/api**  
   - Example: **http://localhost:8080/api/auth/login**

6. Default admin (created on first run):
   - **Username:** `admin`
   - **Password:** `admin123`

---

## 3. Frontend

1. Serve the `frontend` folder with any static server. Examples:

   **Using VS Code Live Server**
   - Install the “Live Server” extension.
   - Right‑click `frontend/index.html` → “Open with Live Server”.
   - Note the URL (e.g. `http://127.0.0.1:5500/frontend/` or `http://127.0.0.1:5500/` depending on how you opened it).

   **Using Node.js (npx)**
   ```bash
   cd frontend
   npx serve -p 5500
   ```
   Then open: **http://localhost:5500**

2. If your frontend runs on a **different host or port**, update the API URL in `frontend/js/api.js`:
   ```javascript
   const API_BASE = 'http://localhost:8080/api';
   ```
   And in `frontend/js/login.js`, use the same base for the login URL (e.g. `http://localhost:8080/api/auth/login`).

3. Open the frontend URL in the browser and log in with **admin** / **admin123**.

---

## 4. Project Structure (Complete)

```
SDPMS new/
├── backend/                          # Spring Boot REST API
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/sdpms/
│       │   ├── SdpmsApplication.java
│       │   ├── config/               # Security, CORS, DataLoader
│       │   ├── controller/           # Auth, Admin, Faculty, Student
│       │   ├── dto/
│       │   ├── exception/
│       │   ├── model/                # User, Student, Faculty, Department, etc.
│       │   ├── repository/
│       │   ├── security/            # JWT
│       │   └── service/
│       └── resources/
│           └── application.properties
│
├── frontend/                         # HTML/CSS/JavaScript
│   ├── index.html                   # Login page
│   ├── dashboard.html                # Role-based dashboard
│   ├── css/
│   │   └── style.css
│   └── js/
│       ├── api.js                   # API base URL & auth headers
│       ├── login.js
│       └── dashboard.js
│
├── database/
│   ├── schema.sql                   # Full MySQL schema (all tables)
│   └── seed.sql                     # Optional sample data
│
├── README.md
└── PROJECT_SETUP.md                 # This file
```

---

## 5. Quick Test

1. **Login:** Open frontend → Login with `admin` / `admin123`.
2. **Admin:** You should see the dashboard with Departments, Students, Faculty, Subjects.
3. **Create a student (optional):** Use the Admin section or call:
   - `POST http://localhost:8080/api/admin/students`
   - Headers: `Authorization: Bearer <your_token>`, `Content-Type: application/json`
   - Body (example):  
     `{"username":"student1","password":"pass123","fullName":"Test Student","rollNumber":"R001","departmentId":1,"currentSemester":1}`
4. **Student login:** Log out, log in as `student1` / `pass123` and open **My Progress** to see the student dashboard.

---

## 6. Troubleshooting

| Issue | What to do |
|-------|------------|
| Backend won’t start | Check Java 17+, Maven, and MySQL running. Confirm `application.properties` username/password. |
| “Access denied” MySQL | Correct `spring.datasource.username` and `spring.datasource.password` in `application.properties`. |
| Frontend can’t reach API | Ensure backend is running on port 8080. If frontend is on another origin, CORS is allowed for common ports (5500, 3000); otherwise add it in `SecurityConfig.java`. |
| 401 on dashboard | Log in again; token may have expired or be missing. |
| Blank dashboard | Open browser DevTools (F12) → Console/Network and check for errors or failed API calls. |

---

## Summary

- **Database:** Create `sdpms` (and optionally run `database/schema.sql` and `database/seed.sql`).
- **Backend:** `cd backend` → `mvn spring-boot:run` → runs at **http://localhost:8080/api**.
- **Frontend:** Serve `frontend` folder (e.g. Live Server or `npx serve`) and open in browser.
- **Login:** `admin` / `admin123` for full access; create students/faculty via Admin to test other roles.

You now have the complete project: **frontend**, **backend**, and **database** working together.
