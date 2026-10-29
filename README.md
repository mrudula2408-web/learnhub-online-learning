# NextLearn — Modern Online Learning Platform

[![FastAPI](https://img.shields.io/badge/FastAPI-0.115.6-009688.svg?logo=fastapi&logoColor=white)](https://fastapi.tiangolo.com)
[![React](https://img.shields.io/badge/React-18.3.1-61DAFB.svg?logo=react&logoColor=black)](https://react.dev)
[![Vite](https://img.shields.io/badge/Vite-6.0-646CFF.svg?logo=vite&logoColor=white)](https://vitejs.dev)
[![PostgreSQL](https://img.shields.io/badge/Database-Supabase%20PostgreSQL-3ECF8E.svg?logo=supabase&logoColor=white)](https://supabase.com)
[![Tailwind CSS](https://img.shields.io/badge/Styling-Modern%20Dark%20Theme-06B6D4.svg?logo=tailwindcss&logoColor=white)](https://tailwindcss.com)

A production-grade, full-stack online learning platform engineered for college case study evaluation and faculty demonstration. Accessible via public links on laptops, tablets, and mobile devices without requiring any local server or laptop to run.

---

## 🌟 Key Features

1. **Deployment-First Architecture**:
   - **Frontend**: Single-Page Application (SPA) built with React 18, Vite, and responsive dark glassmorphic styling, deployable to **Vercel** with full client-side routing support.
   - **Backend**: High-performance RESTful API built with **Python 3.12** and **FastAPI**, deployable to **Render** or any Python PaaS with automatic OpenAPI/Swagger documentation (`/docs`).
   - **Cloud Database**: Managed relational storage using **Supabase PostgreSQL** with connection pooling and automated migration scripts. Zero local database servers required.

2. **Secure JWT Authentication**:
   - Bcrypt password hashing (`salt + work factor`).
   - Stateless JWT Bearer token authentication with 7-day expiration.
   - Cross-device persistence: Students register once and log in from any phone or computer with all learning progress, enrollments, and quiz results intact.

3. **Curated Course & Module Management**:
   - Realistic multi-category courses: Web Development, Cloud & DevOps, Data Science & AI.
   - Interactive modules featuring verified external textbooks, documentation (MDN, React Docs, FastAPI Docs, NIST, Scikit-Learn), and official tutorials.
   - One-click safe external learning resource launch.

4. **Strict Quiz Unlocking Mechanism**:
   - **Dual-Layer Enforcement**: Quizzes remain strictly locked until the student has marked **100% of required modules** completed.
   - **Tamper-Proof Backend Validation**: The backend does NOT expose questions or options while the quiz is locked, and rejects any unauthorized submission attempts with `403 Forbidden`.
   - Real-time display of incomplete modules with jump links.

5. **Live Student Dashboard & Progress Tracking**:
   - Progress percentage formula:
     $$\text{Progress} = \left(\frac{\text{Completed Required Modules}}{\text{Total Required Modules}}\right) \times 100$$
   - Course-wise progress tracking with live updates.
   - Quiz attempt history with comprehensive score breakdowns, percentage badges, and question-by-question explanations.

---

## 🏗️ Technology Stack

| Component | Technology | Role |
|-----------|------------|------|
| **Frontend Framework** | React 18.3 + Vite 6 | Lightning-fast reactive UI & state management |
| **Routing** | React Router DOM v6 | Seamless multi-page navigation with SPA rewrite rules |
| **UI Design & Theme** | Modern Dark Mode + CSS Tokens | Responsive cards, badges, gradients, and animations |
| **Backend Framework** | Python 3.12 + FastAPI 0.115 | Asynchronous REST API, high throughput, low latency |
| **Data Validation** | Pydantic v2 + Settings | Strict request/response typing and payload sanitization |
| **Database ORM** | SQLAlchemy 2.0 | Type-safe query building, connection pooling, and migrations |
| **Database Engine** | Supabase (PostgreSQL 15+) | Persistent cloud SQL database with foreign key cascades |
| **Testing** | Pytest + HTTPX + Starlette TestClient | Automated integration test suite |

---

## 📁 Repository Structure

```
nextlearn/
├── frontend/                     # React + Vite Client Application
│   ├── src/
│   │   ├── assets/               # Images and SVGs
│   │   ├── components/           # Navbar, CourseCard, Quiz, StudentDashboard, AlertMessage
│   │   ├── pages/                # HomePage, CoursesPage, LearningPage, QuizPage, DashboardPage, Auth
│   │   ├── services/             # api.js, authService, courseService, enrollmentService, quizService
│   │   ├── App.jsx               # Main router and session provider
│   │   └── styles.css            # Dark mode design system and responsive layout tokens
│   ├── package.json              # Frontend scripts & dependencies
│   ├── vercel.json               # SPA routing rewrite configuration
│   └── .env.example              # Frontend environment template
├── backend/                      # Python FastAPI Application
│   ├── app/
│   │   ├── api/                  # REST endpoints (auth, courses, enrollments, progress, quizzes, compat)
│   │   ├── core/                 # config, database connection, JWT security & password hashing
│   │   ├── models/               # SQLAlchemy relational models
│   │   ├── schemas/              # Pydantic validation schemas
│   │   ├── services/             # Automatic database seeding
│   │   └── main.py               # FastAPI application entrypoint & lifespan
│   ├── tests/                    # Automated integration test suite (test_api.py)
│   ├── requirements.txt          # Python dependencies
│   ├── Procfile                  # Cloud deployment start command
│   ├── render.yaml               # Render PaaS configuration
│   └── .env.example              # Backend environment template
├── database/                     # Cloud Database Scripts
│   ├── schema.sql                # Complete PostgreSQL DDL (tables, indexes, foreign keys)
│   └── seed.sql                  # Initial seed data for courses, modules, resources, and quizzes
├── render.yaml                   # Root PaaS deployment blueprint
├── vercel.json                   # Root Vercel deployment configuration
└── README.md                     # Documentation
```

---

## 🚀 Quick Deployment Guide

### 1. Cloud Database Setup (Supabase) — ~1 Minute
1. Sign in to [Supabase](https://supabase.com) (free tier).
2. Create a new project called `nextlearn`.
3. Open the **SQL Editor** tab from the left sidebar.
4. Copy and paste the contents of `database/schema.sql`, then click **Run**.
5. Copy and paste the contents of `database/seed.sql`, then click **Run**.
6. Go to **Project Settings > Database** and copy your **Connection String** (`URI` mode):
   ```
   postgresql://postgres:[YOUR-PASSWORD]@db.[YOUR-PROJECT-REF].supabase.co:5432/postgres
   ```

### 2. Backend Deployment (Render) — ~2 Minutes
1. Push this repository to GitHub.
2. Sign in to [Render](https://render.com) (free tier).
3. Click **New + > Web Service** and select your GitHub repository.
4. Configure:
   - **Root Directory**: `backend`
   - **Runtime**: `Python 3`
   - **Build Command**: `pip install -r requirements.txt`
   - **Start Command**: `uvicorn app.main:app --host 0.0.0.0 --port $PORT`
5. Add Environment Variables:
   - `DATABASE_URL`: Your Supabase connection string.
   - `SECRET_KEY`: A random 32-character secret key.
   - `ALLOWED_ORIGINS`: `*` (or your Vercel URL).
6. Click **Deploy Web Service**. Your public backend will be live at:
   `https://nextlearn-api.onrender.com` (Health check at `/health`).

### 3. Frontend Deployment (Vercel) — ~1 Minute
1. Sign in to [Vercel](https://vercel.com).
2. Click **Add New... > Project** and import your GitHub repository.
3. Configure:
   - **Root Directory**: `frontend`
   - **Framework Preset**: `Vite`
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
4. Add Environment Variable:
   - `VITE_API_URL`: Your Render backend URL + `/api` (e.g., `https://nextlearn-api.onrender.com/api`).
5. Click **Deploy**. Your public platform will be instantly live on a public `*.vercel.app` URL.

---

## 💻 Local Development Setup

### Backend Setup
```bash
cd backend
python -m venv .venv
# On Windows:
.\.venv\Scripts\activate
# On Linux/macOS:
source .venv/bin/activate

pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```
- API Docs: `http://localhost:8000/docs`
- Health check: `http://localhost:8000/health`

### Frontend Setup
```bash
cd frontend
npm install
npm run dev
```
- Frontend app: `http://localhost:5173`

---

## 🧪 Automated Testing

Run the comprehensive integration test suite:
```bash
cd backend
pytest -v tests/test_api.py
```

---

## 🎓 Faculty Evaluation Credentials

For rapid faculty viva and assessment, a pre-seeded student account is available:
- **Email**: `student@nextlearn.edu`
- **Password**: `NextLearn2025!`
- *Alternative*: Any faculty member can register their own custom email and password in 5 seconds to test clean registration and state persistence.
