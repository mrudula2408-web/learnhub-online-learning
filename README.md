# LearnHub – Online Learning Platform

A complete, working college project designed for 3rd-year B.Tech CSE evaluation and viva.

---

## 📌 Project Overview

| Component | Technology | Port | Description |
|-----------|-----------|------|-------------|
| **Frontend** | React JS + Vite, HTML5, CSS3, JavaScript | `5173` | Clean UI with reusable components, responsive navbar, course cards, login/register, quiz interface, and student dashboard |
| **Backend** | Java, Spring MVC, Spring Boot, Spring Data JPA | `8081` | REST APIs for authentication (BCrypt hashing), course listing, duplicate-proof enrollment, quiz scoring, and progress tracking |
| **Database** | MySQL 8.x | `3306` | Relational database `learnhub_db` with **only 5 basic tables** |
| **DB Client** | MySQL Workbench | - | Visual tool to verify saved records with `SELECT` queries |

---

## 🗄️ Database Architecture (7 Tables)

`learnhub_db` uses a clean relational structure:

```
learnhub_db
 ├── 1. students            (id, name, email, password_hash, created_at)
 ├── 2. courses             (id, title, description, instructor, duration)
 ├── 3. enrollments         (id, student_id, course_id, enrolled_at)
 ├── 4. course_modules      (id, course_id, module_number, title, description, link1_title, link1_url, link2_title, link2_url)
 ├── 5. module_completions  (id, student_id, module_id, course_id, completed_at)
 ├── 6. learning_progress   (id, student_id, course_id, completion_percentage, completion_status, last_updated)
 └── 7. quiz_attempts       (id, student_id, course_id, quiz_title, score, total_questions, attempt_date)
```

- **Relationships:**
  - `course_modules`: 3 modules per course with verified textbook and tutorial links.
  - `module_completions`: Tracks completed modules per student with `UNIQUE(student_id, module_id)` to prevent duplicate credit.
  - `learning_progress`: Automatically calculates progress: `(completed_modules / total_modules) * 100`.
  - `quiz_attempts`: Saves score after all 3 modules are completed and the quiz is submitted.

All student registrations, course enrollments, quiz scores, and learning progress updates are **actually persisted in MySQL**.

---

## 📁 Simple Project Structure

```
LearnHub/
├── database.sql             # SQL script to create learnhub_db, 5 tables, & sample courses
├── README.md                # Project setup, execution, and viva guide
├── backend/                 # Java Spring Boot application (Spring MVC + JPA)
│   ├── pom.xml              # Maven dependencies (Spring Web, JPA, Security, MySQL Connector)
│   ├── mvnw / mvnw.cmd      # Maven wrapper scripts
│   └── src/
│       └── main/
│           ├── java/com/learnhub/
│           │   ├── LearnHubApplication.java   # Spring Boot entry point
│           │   ├── config/
│           │   │   ├── SecurityConfig.java     # BCrypt encoder & CORS filter
│           │   │   └── WebConfig.java          # CORS mapping for React (localhost:5173)
│           │   ├── controller/
│           │   │   ├── AuthController.java     # /api/auth/register, /api/auth/login, /api/auth/me
│           │   │   ├── CourseController.java   # /api/courses (GET all & by ID)
│           │   │   ├── EnrollmentController.java # /api/enrollments (POST enroll & GET my)
│           │   │   ├── QuizController.java     # /api/quizzes (GET questions & POST submit)
│           │   │   └── ProgressController.java # /api/progress (GET & PUT progress)
│           │   ├── dto/                        # Request/Response data transfer objects
│           │   ├── entity/                     # 5 JPA Entities (Student, Course, Enrollment, QuizAttempt, LearningProgress)
│           │   ├── repository/                 # 5 Spring Data JPA Repositories
│           │   └── service/                    # Business logic layer
│           └── resources/
│               └── application.properties     # MySQL connection configuration
└── frontend/                # React JS Application (Vite)
    ├── package.json         # React dependencies (React 18, React Router)
    ├── vite.config.js       # Vite configuration
    ├── index.html           # Single Page Application HTML root
    └── src/
        ├── App.jsx          # Route configuration
        ├── main.jsx         # React root DOM render
        ├── styles.css       # Clean, modern styling
        ├── components/      # Reusable React components
        │   ├── Navbar.jsx           # Top navigation bar
        │   ├── CourseCard.jsx       # Course card reusable component
        │   ├── CourseList.jsx       # Course grid display
        │   ├── LoginForm.jsx        # Student login form
        │   ├── RegisterForm.jsx     # Student registration form
        │   ├── Quiz.jsx             # Multiple-choice quiz interface
        │   ├── QuizQuestion.jsx     # Individual question component
        │   ├── ProgressCard.jsx     # Progress bar & updater
        │   └── StudentDashboard.jsx # Enrolled courses, quiz scores, progress
        ├── pages/           # Pages (Home, Login, Register, Courses, Dashboard, Quiz)
        └── services/        # Fetch API service layer calling Spring Boot endpoints
```

---

## 🚀 Step-by-Step Setup and Execution (Windows)

### Prerequisites Check
Open PowerShell or Command Prompt:
```powershell
java -version       # JDK 17 or JDK 21+
node -v             # Node.js 18+
npm -v              # npm 9+
mysql --version     # MySQL 8.x
```

---

### Step 1: Set Up MySQL Database Using `database.sql`

#### Option A: Using MySQL Workbench (Visual & Recommended)
1. Open **MySQL Workbench**.
2. Connect to your local MySQL instance (e.g. `root` @ `localhost:3306`).
3. Click **File** -> **Open SQL Script...** (or press `Ctrl + Shift + O`).
4. Select `database.sql` from the `LearnHub` project directory:
   `C:\Users\mrudu\.gemini\antigravity\scratch\LearnHub\database.sql`
5. Click the **Execute** button (the yellow lightning bolt icon ⚡) to run the script.
6. In the left **Navigator** pane, under **SCHEMAS**, right-click and click **Refresh All**.
7. Expand `learnhub_db` -> `Tables`. You will see exactly **5 tables**:
   - `students`
   - `courses`
   - `enrollments`
   - `quiz_attempts`
   - `learning_progress`

#### Option B: Using Windows PowerShell / Command Prompt
```powershell
# Navigate to LearnHub directory
cd "C:\Users\mrudu\.gemini\antigravity\scratch\LearnHub"

# Execute database.sql using MySQL CLI
mysql -u root -p < database.sql
```
*(Enter your MySQL root password when prompted)*

---

### Step 2: Configure MySQL Password for Backend

Open `backend/src/main/resources/application.properties`.

The properties file is already configured with sensible defaults:
```properties
server.port=8081
spring.datasource.url=jdbc:mysql://localhost:3306/learnhub_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=${LEARNHUB_DB_PASSWORD:}
spring.jpa.hibernate.ddl-auto=update
```

You can either:
- **Set the environment variable in PowerShell before running:**
  ```powershell
  $env:LEARNHUB_DB_PASSWORD="your_mysql_password"
  ```
- **Or edit `application.properties` directly:**
  Change `spring.datasource.password=${LEARNHUB_DB_PASSWORD:}` to your password:
  ```properties
  spring.datasource.password=your_mysql_password
  ```

---

### Step 3: Run the Spring Boot Backend

Open a PowerShell window:

```powershell
# 1. Navigate to backend directory
cd "C:\Users\mrudu\.gemini\antigravity\scratch\LearnHub\backend"

# 2. Start Spring Boot using Maven wrapper (or installed maven: mvn spring-boot:run)
.\mvnw.cmd spring-boot:run
```

You will see:
```text
Tomcat started on port 8081 (http) with context path '/'
Started LearnHubApplication in ... seconds
```
The REST API is now live at `http://localhost:8081`.

---

### Step 4: Run the React Frontend

Open a **second** PowerShell window:

```powershell
# 1. Navigate to frontend directory
cd "C:\Users\mrudu\.gemini\antigravity\scratch\LearnHub\frontend"

# 2. Install dependencies (already built, but good practice)
npm install

# 3. Start React Vite development server
npm run dev
```

You will see:
```text
  VITE v6.x.x  ready in ... ms

  ➜  Local:   http://localhost:5173/
```

Open your browser and navigate to: **`http://localhost:5173`**

---

## 🧪 Testing the Complete Workflow (Viva Demo Checklist)

Follow these steps to demonstrate the working project to your faculty:

1. **Home Page (`http://localhost:5173`)**:
   - The navigation bar shows **LearnHub**, **Home**, **Courses**, **Login**, and **Register**.
   - Available courses fetched directly from the MySQL database via Spring Boot (`/api/courses`) are displayed in reusable `CourseCard` components.
2. **Student Registration (`/register`)**:
   - Register a new student (e.g., Name: `Rahul Sharma`, Email: `rahul@example.com`, Password: `Password@123`).
   - Notice that the password is automatically hashed using **BCrypt** before insertion into MySQL.
3. **Student Login (`/login`)**:
   - Log in with the registered credentials.
   - The navbar updates to show the student's name, **Dashboard**, and **Logout** button.
4. **Course Enrollment (`/courses`)**:
   - Click **Enroll Now** on any course (e.g. "Introduction to Java Programming").
   - Backend saves the enrollment in MySQL `enrollments` table and creates an entry in `learning_progress`.
   - If you click "Enroll Now" again, the backend prevents duplicate enrollment and displays: *"You are already enrolled in this course."*
5. **Simple Quiz (`/quiz/1`)**:
   - Navigate to the course assessment quiz.
   - Answer the 5 multiple-choice questions and click **Submit Quiz**.
   - The backend validates each answer against the correct answer, calculates your score (e.g., 4/5 = 80%), and saves the attempt in the `quiz_attempts` table.
   - The result screen shows your score and pass badge.
6. **Student Dashboard (`/dashboard`)**:
   - View your enrolled courses.
   - Update your learning progress percentage (e.g., set to 75% or 100% Completed) – this updates MySQL `learning_progress` table.
   - View your saved quiz attempt history and scores.

---

## 🔍 How to Open MySQL Workbench and Show Faculty Saved Data

During project evaluation, open **MySQL Workbench**, connect to `localhost:3306`, and run the following queries in a SQL Query tab:

```sql
USE learnhub_db;

-- 1. View registered students (notice password_hash is securely encrypted)
SELECT id, name, email, password_hash, created_at FROM students;

-- 2. View all courses available on the platform
SELECT id, title, instructor, duration FROM courses;

-- 3. View student enrollments (shows which student enrolled in which course)
SELECT e.id, s.name AS student_name, s.email, c.title AS course_title, e.enrolled_at
FROM enrollments e
JOIN students s ON e.student_id = s.id
JOIN courses c ON e.course_id = c.id;

-- 4. View quiz scores saved by the Java backend
SELECT qa.id, s.name AS student_name, qa.quiz_title, qa.score, qa.total_questions, qa.attempt_date
FROM quiz_attempts qa
JOIN students s ON qa.student_id = s.id;

-- 5. View learning progress updates
SELECT lp.id, s.name AS student_name, c.title AS course_title, lp.completion_percentage, lp.completion_status, lp.last_updated
FROM learning_progress lp
JOIN students s ON lp.student_id = s.id
JOIN courses c ON lp.course_id = c.id;
```

---

## 💡 Viva Questions and Answers for 3rd-Year CSE

1. **Q: What architecture does LearnHub follow?**
   - **A:** 3-tier MVC architecture:
     - **Presentation Tier:** React JS (Single Page Application using reusable components and hooks).
     - **Business / Application Tier:** Spring MVC REST Controllers (`@RestController`), Services (`@Service`), and Spring Data JPA.
     - **Data Tier:** MySQL database (`learnhub_db`) managed via Hibernate ORM.
2. **Q: How does the React frontend communicate with Java Spring Boot?**
   - **A:** React uses standard HTTP REST API calls (`fetch` with JSON request bodies and session cookies) to endpoints exposed by Spring Boot controllers (`/api/auth`, `/api/courses`, `/api/enrollments`, `/api/quizzes`, `/api/progress`).
3. **Q: How is security handled?**
   - **A:** Passwords are never stored in plain text. Spring Security's `BCryptPasswordEncoder` hashes passwords with a cryptographic salt before persisting into the `students` table. HTTP sessions track the authenticated student.
4. **Q: Where does quiz evaluation happen?**
   - **A:** Strictly on the Java backend in `QuizService`. Correct answers are never sent to the client browser, preventing inspection through browser developer tools.
5. **Q: How is duplicate course enrollment prevented?**
   - **A:** Both at the application level in `EnrollmentService` (`existsByStudentIdAndCourseId`) and at the database level with a `UNIQUE(student_id, course_id)` constraint on the `enrollments` table.
