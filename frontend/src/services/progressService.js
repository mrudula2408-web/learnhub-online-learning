/**
 * progressService.js — Learning Progress API calls
 */
import apiFetch from './api.js';

const progressService = {

  /** Get all progress records for the logged-in student */
  getMyProgress: () =>
    apiFetch('/progress/my'),

  /**
   * Update the progress for a course.
   * @param {number} courseId
   * @param {number} completionPercentage - 0 to 100
   * @param {string} completionStatus - "IN_PROGRESS" or "COMPLETED"
   */
  updateProgress: (courseId, completionPercentage, completionStatus) =>
    apiFetch(`/progress/${courseId}`, {
      method: 'PUT',
      body: JSON.stringify({ completionPercentage, completionStatus }),
    }),
};

export default progressService;
