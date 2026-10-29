/**
 * enrollmentService.js — Enrollment API calls
 */
import apiFetch from './api.js';

const enrollmentService = {

  /** Enroll the logged-in student in a course */
  enroll: (courseId) =>
    apiFetch('/enrollments', {
      method: 'POST',
      body: JSON.stringify({ course_id: courseId, courseId }),
    }),

  /** Get all courses the logged-in student is enrolled in */
  getMyEnrollments: () =>
    apiFetch('/enrollments/my'),
};

export default enrollmentService;
