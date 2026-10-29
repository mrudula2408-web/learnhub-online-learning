/**
 * RegisterForm.jsx — Student registration form component.
 * Validates inputs, submits to the backend, and shows feedback.
 */
import React, { useState } from 'react';
import AlertMessage from './AlertMessage.jsx';
import authService from '../services/authService.js';

function RegisterForm({ onSuccess }) {
  // Form field state
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirm, setConfirm] = useState('');

  // UI state
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault(); // Prevent default browser form submission
    setError('');
    setSuccess('');

    // Client-side validation
    if (!name.trim() || !email.trim() || !password.trim()) {
      setError('All fields are required.');
      return;
    }
    if (password !== confirm) {
      setError('Passwords do not match.');
      return;
    }
    if (password.length < 6) {
      setError('Password must be at least 6 characters.');
      return;
    }

    setLoading(true);
    try {
      const data = await authService.register(name.trim(), email.trim(), password);
      setSuccess(data.message || 'Registration successful! You can now log in.');
      // Clear form
      setName(''); setEmail(''); setPassword(''); setConfirm('');
      // Notify parent component
      if (onSuccess) onSuccess(data);
    } catch (err) {
      setError(err.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} id="register-form" noValidate>
      <AlertMessage type="success" message={success} />
      <AlertMessage type="error" message={error} />

      {/* Name Field */}
      <div className="form-group">
        <label className="form-label" htmlFor="reg-name">Full Name</label>
        <input
          id="reg-name"
          type="text"
          className="form-input"
          placeholder="Enter your full name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
          autoComplete="name"
        />
      </div>

      {/* Email Field */}
      <div className="form-group">
        <label className="form-label" htmlFor="reg-email">Email Address</label>
        <input
          id="reg-email"
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
        <label className="form-label" htmlFor="reg-password">Password</label>
        <input
          id="reg-password"
          type="password"
          className="form-input"
          placeholder="At least 6 characters"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          autoComplete="new-password"
        />
      </div>

      {/* Confirm Password Field */}
      <div className="form-group">
        <label className="form-label" htmlFor="reg-confirm">Confirm Password</label>
        <input
          id="reg-confirm"
          type="password"
          className="form-input"
          placeholder="Repeat your password"
          value={confirm}
          onChange={(e) => setConfirm(e.target.value)}
          required
          autoComplete="new-password"
        />
      </div>

      <button
        id="register-submit-btn"
        type="submit"
        className="btn btn-primary btn-full"
        disabled={loading}
      >
        {loading ? '⏳ Creating Account...' : '🚀 Create Account'}
      </button>
    </form>
  );
}

export default RegisterForm;
