/**
 * api.js — Base API utility module
 *
 * All fetch calls to the Spring Boot backend go through here.
 * The backend runs on http://localhost:8081
 * credentials: 'include' is REQUIRED so the browser sends the session cookie.
 */

// In development: http://localhost:8081/api
// In production: set VITE_API_URL in environment or .env file (e.g. on Vercel)
const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8081/api';

/**
 * Generic fetch wrapper.
 * @param {string} endpoint - API path (e.g. '/auth/login')
 * @param {object} options  - fetch options (method, body, etc.)
 * @returns {Promise<any>}  - parsed JSON response
 */
async function apiFetch(endpoint, options = {}) {
  let response;
  try {
    response = await fetch(`${BASE_URL}${endpoint}`, {
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
      // credentials: 'include' sends the session cookie with every request
      // This is how the backend knows which student is logged in
      credentials: 'include',
      ...options,
    });
  } catch (err) {
    throw new Error(`Cannot connect to backend server at ${BASE_URL}. Please ensure the Java Spring Boot backend is running.`);
  }

  // Try to parse JSON response
  const data = await response.json().catch(() => null);

  if (!response.ok) {
    // Throw an error with the message from the backend (if available)
    throw new Error(data?.message || `Request failed with status ${response.status}`);
  }

  return data;
}

export default apiFetch;
