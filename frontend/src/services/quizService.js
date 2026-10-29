/**
 * quizService.js — Quiz API calls
 */
import apiFetch from './api.js';

const quizService = {

  /** Get all quizzes for a course */
  getQuizzesByCourse: (courseId) =>
    apiFetch(`/quizzes/course/${courseId}`),

  /** Get questions for a quiz (correct answers are NOT included) */
  getQuizQuestions: (quizId) =>
    apiFetch(`/quizzes/${quizId}/questions`),

  /**
   * Submit quiz answers to the backend for scoring.
   * answers is an object: { questionId: "A" | "B" | "C" | "D" }
   */
  submitQuiz: (quizId, answers) =>
    apiFetch(`/quizzes/${quizId}/submit`, {
      method: 'POST',
      body: JSON.stringify({ answers }),
    }),

  /** Get all quiz attempts by the logged-in student */
  getMyAttempts: () =>
    apiFetch('/quizzes/attempts/my'),
};

export default quizService;
