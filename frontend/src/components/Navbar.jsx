/**
 * Navbar.jsx — Top navigation bar
 * Shows navigation links and handles login/logout display.
 * Receives 'student' (null if logged out) and 'onLogout' from App.
 */
import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import authService from '../services/authService.js';

function Navbar({ student, onLogout }) {
  const navigate = useNavigate();

  // Handle logout: call API, clear state, redirect to login
  const handleLogout = async () => {
    try {
      await authService.logout();
    } catch (err) {
      // Ignore logout errors — still clear state locally
    }
    onLogout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-inner">
        {/* Brand / Logo */}
        <NavLink to={student ? "/" : "/login"} className="navbar-brand">
          ⚡ NextLearn
        </NavLink>


        {/* Navigation Links (visible when logged in) */}
        {student && (
          <ul className="navbar-links">
            <li>
              <NavLink to="/" end className={({ isActive }) => isActive ? 'active' : ''}>
                Home
              </NavLink>
            </li>
            <li>
              <NavLink to="/courses" className={({ isActive }) => isActive ? 'active' : ''}>
                Courses
              </NavLink>
            </li>
            <li>
              <NavLink to="/dashboard" className={({ isActive }) => isActive ? 'active' : ''}>
                Dashboard
              </NavLink>
            </li>
          </ul>
        )}

        {/* Auth Section */}
        <div className="navbar-user">
          {student ? (
            <>
              <span className="navbar-user-name">👋 {student.name}</span>
              <button
                id="navbar-logout-btn"
                className="btn btn-secondary btn-sm"
                onClick={handleLogout}
              >
                Logout
              </button>
            </>
          ) : (
            <>
              <NavLink to="/login">
                <button className="btn btn-secondary btn-sm">Login</button>
              </NavLink>
              <NavLink to="/register">
                <button className="btn btn-primary btn-sm">Register</button>
              </NavLink>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
