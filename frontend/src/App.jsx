/**
 * App.jsx — Root component of the LearnHub React application.
 *
 * This component:
 * 1. Sets up React Router for page navigation
 * 2. Manages the global "logged-in student" state
 * 3. Renders the Navbar on every page
 * 4. Defines routes for each page component
 *
 * React Router concepts:
 * - BrowserRouter: enables URL-based navigation
 * - Routes / Route: maps URL paths to components
 * - useEffect: runs the session check on first load
 */
import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';

// Components
import Navbar from './components/Navbar.jsx';

// Pages
import HomePage      from './pages/HomePage.jsx';
import CoursesPage   from './pages/CoursesPage.jsx';
import LoginPage     from './pages/LoginPage.jsx';
import RegisterPage  from './pages/RegisterPage.jsx';
import DashboardPage from './pages/DashboardPage.jsx';
import QuizPage      from './pages/QuizPage.jsx';
import LearningPage  from './pages/LearningPage.jsx';

// Services
import authService from './services/authService.js';

function App() {
  // Global state: null means not logged in, object means logged in
  const [student, setStudent] = useState(null);
  const [authLoading, setAuthLoading] = useState(true);

  // On app start, check if there is an active session on the backend
  useEffect(() => {
    authService.getMe()
      .then(data => {
        // Session exists — restore the logged-in student
        setStudent({ studentId: data.studentId, name: data.name, email: data.email });
      })
      .catch(() => {
        // No active session or backend not running — stay logged out
        setStudent(null);
      })
      .finally(() => setAuthLoading(false));
  }, []);

  // Called after successful login
  const handleLogin = (studentData) => {
    setStudent(studentData);
  };

  // Called after logout
  const handleLogout = () => {
    setStudent(null);
  };

  // Show nothing while checking session (prevents flash of login page)
  if (authLoading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <div className="spinner"></div>
      </div>
    );
  }

  return (
    <BrowserRouter>
      {/* Navbar is shown on all pages */}
      <Navbar student={student} onLogout={handleLogout} />

      {/* Page Routes */}
      <Routes>
        {/* Authentication routes */}
        <Route path="/login"    element={!student ? <LoginPage onLogin={handleLogin} /> : <Navigate to="/" replace />} />
        <Route path="/register" element={!student ? <RegisterPage onRegister={handleLogin} /> : <Navigate to="/" replace />} />


        {/* Protected routes — visitors must be logged in */}
        <Route path="/" element={student ? <HomePage student={student} /> : <Navigate to="/login" replace />} />
        <Route path="/courses" element={student ? <CoursesPage student={student} /> : <Navigate to="/login" replace />} />
        <Route path="/dashboard" element={student ? <DashboardPage student={student} /> : <Navigate to="/login" replace />} />
        <Route path="/learn/:courseId" element={student ? <LearningPage student={student} /> : <Navigate to="/login" replace />} />
        <Route path="/quiz/:courseId" element={student ? <QuizPage student={student} /> : <Navigate to="/login" replace />} />

        {/* Fallback */}
        <Route path="*" element={<Navigate to={student ? "/" : "/login"} replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
