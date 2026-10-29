/**
 * authService.js — Authentication API service using JWT Bearer tokens
 */
import apiFetch from './api.js';

const authService = {

  /** Register a new student */
  register: async (name, email, password) => {
    const data = await apiFetch('/auth/register', {
      method: 'POST',
      body: JSON.stringify({
        email: email.trim().toLowerCase(),
        password,
        full_name: name.trim(),
      }),
    });

    if (data.access_token) {
      localStorage.setItem('learnhub_token', data.access_token);
      const student = {
        studentId: data.user.id,
        id: data.user.id,
        name: data.user.full_name,
        email: data.user.email,
        role: data.user.role,
      };
      localStorage.setItem('learnhub_user', JSON.stringify(student));
      return student;
    }
    return data;
  },

  /** Log in with email and password */
  login: async (email, password) => {
    const data = await apiFetch('/auth/login', {
      method: 'POST',
      body: JSON.stringify({
        email: email.trim().toLowerCase(),
        password,
      }),
    });

    if (data.access_token) {
      localStorage.setItem('learnhub_token', data.access_token);
      const student = {
        studentId: data.user.id,
        id: data.user.id,
        name: data.user.full_name,
        email: data.user.email,
        role: data.user.role,
      };
      localStorage.setItem('learnhub_user', JSON.stringify(student));
      return student;
    }
    return data;
  },

  /** Log out the current session */
  logout: async () => {
    localStorage.removeItem('learnhub_token');
    localStorage.removeItem('learnhub_user');
    return { success: true };
  },

  /** Get the currently logged-in student's info from token verification */
  getMe: async () => {
    const token = localStorage.getItem('learnhub_token');
    if (!token) {
      throw new Error('No active session token');
    }
    const user = await apiFetch('/auth/me');
    const student = {
      studentId: user.id,
      id: user.id,
      name: user.full_name,
      email: user.email,
      role: user.role,
    };
    localStorage.setItem('learnhub_user', JSON.stringify(student));
    return student;
  },
};

export default authService;
