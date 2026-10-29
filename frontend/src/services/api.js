/**
 * api.js — Base API utility module for NextLearn
 * 
 * Interacts with the Python FastAPI REST backend.
 * Automatically injects the JWT Bearer token from localStorage.
 */

// In development: http://localhost:8000/api
// In production: configured via VITE_API_URL on Vercel
const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8000/api';

/**
 * Generic fetch wrapper with JWT Bearer authentication.
 * @param {string} endpoint - API path (e.g. '/auth/login', '/courses')
 * @param {object} options  - fetch options (method, body, headers, etc.)
 * @returns {Promise<any>}  - parsed JSON response
 */
async function apiFetch(endpoint, options = {}) {
  const token = localStorage.getItem('nextlearn_token') || localStorage.getItem('learnhub_token');
  
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { 'Authorization': `Bearer ${token}` } : {}),
    ...options.headers,
  };

  let response;
  try {
    response = await fetch(`${BASE_URL}${endpoint}`, {
      ...options,
      headers,
    });
  } catch (err) {
    throw new Error(`Cannot reach NextLearn API at ${BASE_URL}. Please check network connection or verify the backend service is running.`);
  }

  // Handle 401 Unauthorized
  if (response.status === 401 && endpoint !== '/auth/login' && endpoint !== '/auth/register') {
    localStorage.removeItem('nextlearn_token');
    localStorage.removeItem('nextlearn_user');
  }


  // Parse response
  let data = null;
  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    data = await response.json().catch(() => null);
  } else {
    data = await response.text().catch(() => null);
  }

  if (!response.ok) {
    const errorMsg = data?.detail || data?.message || (typeof data === 'string' ? data : `Request failed with status ${response.status}`);
    throw new Error(errorMsg);
  }

  return data;
}

export default apiFetch;
