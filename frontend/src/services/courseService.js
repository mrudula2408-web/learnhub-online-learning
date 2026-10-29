/**
 * courseService.js — Course API calls
 */
import apiFetch from './api.js';

const courseService = {

  /** Get all courses, optionally filtered by search keyword */
  getCourses: (search = '') => {
    const query = search ? `?search=${encodeURIComponent(search)}` : '';
    return apiFetch(`/courses${query}`);
  },

  /** Get details of a single course by ID */
  getCourseById: (id) =>
    apiFetch(`/courses/${id}`),
};

export default courseService;
