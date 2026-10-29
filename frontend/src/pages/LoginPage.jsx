/**
 * LoginPage.jsx — Student login page.
 */
import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import LoginForm from '../components/LoginForm.jsx';

function LoginPage({ onLogin }) {
  const navigate = useNavigate();

  // Called by LoginForm after successful login
  const handleSuccess = (data) => {
    // data contains { studentId, name, email }
    onLogin({ studentId: data.studentId, name: data.name, email: data.email });
    navigate('/'); // Redirect to home page after login
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="auth-card-header">
          <div style={{ fontSize: '2.5rem', marginBottom: '0.5rem' }}>🔐</div>
          <h1>Welcome Back</h1>
          <p>Sign in to your LearnHub account</p>
        </div>

        <LoginForm onSuccess={handleSuccess} />

        <div className="auth-link">
          Don't have an account?{' '}
          <Link to="/register" style={{ color: 'var(--color-primary-light)', fontWeight: 600 }}>
            Register here
          </Link>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;
