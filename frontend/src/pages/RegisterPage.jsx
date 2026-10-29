/**
 * RegisterPage.jsx — Student registration page.
 */
import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import RegisterForm from '../components/RegisterForm.jsx';

function RegisterPage({ onRegister }) {
  const navigate = useNavigate();

  // After successful registration, log in or redirect
  const handleSuccess = (studentData) => {
    if (onRegister && studentData?.name) {
      onRegister(studentData);
      navigate('/dashboard');
    } else {
      setTimeout(() => navigate('/login'), 1500);
    }
  };


  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="auth-card-header">
          <div style={{ fontSize: '2.5rem', marginBottom: '0.5rem' }}>🎓</div>
          <h1>Create Account</h1>
          <p>Join LearnHub and start learning today</p>
        </div>

        <RegisterForm onSuccess={handleSuccess} />

        <div className="auth-link">
          Already have an account?{' '}
          <Link to="/login" style={{ color: 'var(--color-primary-light)', fontWeight: 600 }}>
            Sign in here
          </Link>
        </div>
      </div>
    </div>
  );
}

export default RegisterPage;
