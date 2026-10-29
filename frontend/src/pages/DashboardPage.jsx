/**
 * DashboardPage.jsx — Student dashboard page.
 * Redirects to login if student is not authenticated.
 */
import React from 'react';
import { useNavigate } from 'react-router-dom';
import StudentDashboard from '../components/StudentDashboard.jsx';

function DashboardPage({ student }) {
  const navigate = useNavigate();

  // If not logged in, show a message and redirect
  if (!student) {
    return (
      <div className="page">
        <div className="container">
          <div className="empty-state">
            <div className="empty-state-icon">🔐</div>
            <h3>Please log in to view your dashboard</h3>
            <p>Your dashboard shows your enrolled courses, progress, and quiz results.</p>
            <button
              className="btn btn-primary mt-2"
              onClick={() => navigate('/login')}
            >
              Go to Login
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="page">
      <div className="container">
        <div className="page-header">
          <h1>📊 My Dashboard</h1>
          <p>Track your learning journey and progress.</p>
        </div>
        <StudentDashboard student={student} />
      </div>
    </div>
  );
}

export default DashboardPage;
