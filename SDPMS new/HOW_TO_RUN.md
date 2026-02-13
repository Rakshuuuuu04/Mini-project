# How to Run SDPMS

Follow these steps **in order**.

---

## Step 1: Install (one-time)

- **Java 17+** – [Download](https://adoptium.net/)
- **Maven** – [Download](https://maven.apache.org/download.cgi) (or use IDE’s built-in Maven)
- **MySQL 8** – [Download](https://dev.mysql.com/downloads/installer/) and install. Remember the **root password** you set.

---

## Step 2: Database

1. Start **MySQL** (from Start menu or `mysql` service).
2. (Optional) Create the database:
   - Open **MySQL Command Line** or **MySQL Workbench**.
   - Run: `CREATE DATABASE IF NOT EXISTS sdpms;`
   - Or run the script: `database/schema.sql`
3. Set your MySQL password in the backend:
   - Open **`backend/src/main/resources/application.properties`**
   - Change this line to your MySQL root password:
   ```properties
   spring.datasource.password=root
   ```
   to
   ```properties
   spring.datasource.password=YOUR_ACTUAL_PASSWORD
   ```
   (If your MySQL password is already `root`, leave it as is.)

---

## Step 3: Run the Backend

**Option A – Command line (Windows PowerShell or CMD)**

```bash
cd "c:\Users\raksh\Downloads\SDPMS new\backend"
mvn spring-boot:run
```

**Option B – From IDE (e.g. IntelliJ / Eclipse / VS Code)**

1. Open the **`backend`** folder as a Maven/Java project.
2. Find **`SdpmsApplication.java`** (under `src/main/java/com/sdpms/`).
3. Right‑click → **Run** (or Run as Java Application).

Wait until you see: **`Started SdpmsApplication`**.  
Backend is running at **http://localhost:8080** (API at **http://localhost:8080/api**).

---

## Step 4: Run the Frontend

**Option A – VS Code Live Server**

1. Install the **Live Server** extension in VS Code.
2. Open the **`frontend`** folder in VS Code (or open the whole project and go to `frontend`).
3. Right‑click **`index.html`** → **Open with Live Server**.
4. Browser opens at something like `http://127.0.0.1:5500` or `http://localhost:5500`.

**Option B – Node.js (if you have Node installed)**

```bash
cd "c:\Users\raksh\Downloads\SDPMS new\frontend"
npx serve -p 5500
```

Then open in browser: **http://localhost:5500**

**Option C – Open file directly (may have CORS issues)**

- Double‑click **`frontend/index.html`** to open in browser.  
- If login fails, use Live Server or `npx serve` instead.

---

## Step 5: Log In

1. In the browser you opened for the frontend, you should see the **login page**.
2. Use:
   - **Username:** `admin`
   - **Password:** `admin123`
3. Click **Sign in**. You should see the **Admin dashboard**.

---

## Summary

| Step | What to do |
|------|------------|
| 1 | Install Java 17, Maven, MySQL |
| 2 | Start MySQL, set `application.properties` password, (optional) create DB |
| 3 | Run backend: `cd backend` → `mvn spring-boot:run` (or run from IDE) |
| 4 | Run frontend: Live Server on `frontend/index.html` or `npx serve` in `frontend` |
| 5 | Open frontend URL in browser → Login: **admin** / **admin123** |

---

## If Something Fails

- **Backend won’t start** → Check Java version (`java -version` should be 17+). Check MySQL is running and password in `application.properties` is correct.
- **“Unknown database” or “Access denied”** → Create database `sdpms` and fix username/password in `application.properties`.
- **Login doesn’t work / blank page** → Make sure backend is running (http://localhost:8080). Use F12 → Console in browser to see errors. Prefer **Live Server** or **npx serve** instead of opening the HTML file directly.
- **Maven not found** → Use your IDE to run `SdpmsApplication` instead of `mvn`; the IDE will use its own Maven/Java.

For more detail, see **PROJECT_SETUP.md**.
