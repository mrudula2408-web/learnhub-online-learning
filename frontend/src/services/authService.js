/**
 * authService.js — Authentication API calls
 */
import apiFetch from './api.js';

const authService = {

  /** Register a new student */
  register: (name, email, password) =>
    apiFetch('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ name, email, password }),
    }),

  /** Log in with email and password */
  login: (email, password) =>
    apiFetch('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    }),

  /** Log out the current session */
  logout: () =>
    apiFetch('/auth/logout', { method: 'POST' }),

  /** Get the currently logged-in student's info */
  getMe: () =>
    apiFetch('/auth/me'),
};

export default authService;
