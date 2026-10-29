/**
 * LoginForm.jsx — Student login form component.
 */
import React, { useState } from 'react';
import AlertMessage from './AlertMessage.jsx';
import authService from '../services/authService.js';

function LoginForm({ onSuccess }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!email.trim() || !password.trim()) {
      setError('Email and password are required.');
      return;
    }

    setLoading(true);
    try {
      const data = await authService.login(email.trim(), password);
      // Pass the logged-in student data to the parent
      if (onSuccess) onSuccess(data);
    } catch (err) {
      setError(err.message || 'Login failed. Please check your credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} id="login-form" noValidate>
      <AlertMessage type="error" message={error} />

      {/* Email Field */}
      <div className="form-group">
        <label className="form-label" htmlFor="login-email">Email Address</label>
        <input
          id="login-email"
          type="email"
          className="form-input"
          placeholder="Enter your email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          autoComplete="email"
        />
      </div>

      {/* Password Field */}
      <div className="form-group">
        <label className="form-label" htmlFor="login-password">Password</label>
        <input
          id="login-password"
          type="password"
          className="form-input"
          placeholder="Enter your password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          autoComplete="current-password"
        />
      </div>

      <button
        id="login-submit-btn"
        type="submit"
        className="btn btn-primary btn-full"
        disabled={loading}
      >
        {loading ? '⏳ Signing In...' : '🔐 Sign In'}
      </button>
    </form>
  );
}

export default LoginForm;
