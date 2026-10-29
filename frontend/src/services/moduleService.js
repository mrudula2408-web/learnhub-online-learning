/**
 * moduleService.js — API calls for course learning modules
 */
import apiFetch from './api.js';

const moduleService = {
  /**
   * Fetch the 3 learning modules for a course with completion status.
   * @param {number} courseId
   */
  getCourseModules: (courseId) =>
    apiFetch(`/courses/${courseId}/modules`),

  /**
   * Mark a module as completed by the student.
   * @param {number} moduleId
   */
  completeModule: (moduleId) =>
    apiFetch(`/modules/${moduleId}/complete`, {
      method: 'POST',
    }),

  /**
   * Get completion summary across all courses for the dashboard.
   */
  getModuleSummary: () =>
    apiFetch('/modules/summary'),

  /**
   * Check if a quiz is locked or unlocked.
   * @param {number} quizId
   */
  getQuizStatus: (quizId) =>
    apiFetch(`/quizzes/${quizId}/status`),
};

export default moduleService;
