/**
 * Quiz.jsx — Quiz container component.
 * Fetches quiz questions, renders them one by one, and handles submission.
 * Scoring happens on the backend — the frontend only sends answers.
 *
 * @param {number}   quizId   - ID of the quiz to load
 * @param {string}   quizTitle - Display title
 * @param {function} onFinish  - Called after quiz is submitted with result data
 */
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import QuizQuestion from './QuizQuestion.jsx';
import AlertMessage from './AlertMessage.jsx';
import quizService from '../services/quizService.js';

function Quiz({ quizId, quizTitle, onFinish }) {
  const navigate = useNavigate();
  const [questions, setQuestions] = useState([]);    // List of questions from backend
  const [answers, setAnswers] = useState({});         // { questionId: 'A' | 'B' | 'C' | 'D' }
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [result, setResult] = useState(null);         // After submission: { score, totalQuestions }

  // Fetch questions when the component mounts
  useEffect(() => {
    const fetchQuestions = async () => {
      setLoading(true);
      try {
        const data = await quizService.getQuizQuestions(quizId);
        setQuestions(data);
      } catch (err) {
        setError('Failed to load quiz questions.');
      } finally {
        setLoading(false);
      }
    };
    fetchQuestions();
  }, [quizId]);

  // Called when a student selects an answer for a question
  const handleSelect = (questionId, answer) => {
    setAnswers(prev => ({ ...prev, [questionId]: answer }));
  };

  // Submit all answers to the backend
  const handleSubmit = async () => {
    const unanswered = questions.filter(q => !answers[q.questionId]);
    if (unanswered.length > 0) {
      setError(`Please answer all questions. ${unanswered.length} question(s) remaining.`);
      return;
    }
    setError('');
    setSubmitting(true);
    try {
      // Send answers object to backend — backend calculates the score
      const data = await quizService.submitQuiz(quizId, answers);
      setResult(data);
      if (onFinish) onFinish(data);
    } catch (err) {
      setError(err.message || 'Failed to submit quiz. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  // --- Loading state ---
  if (loading) {
    return (
      <div className="loading-spinner"><div className="spinner"></div></div>
    );
  }

  // --- Result screen (after submission) ---
  if (result) {
    const percentage = result.percentage || 0;
    const passed = percentage >= 60;
    return (
      <div className="quiz-container">
        <div className="card result-card">
          <div style={{ fontSize: '4rem', marginBottom: '0.5rem' }}>
            {passed ? '🎉' : '📚'}
          </div>
          <div className="result-score">{result.score}/{result.totalQuestions}</div>
          <div className="result-label">{passed ? 'Well Done!' : 'Keep Practicing!'}</div>
          <div className="result-sub">
            You scored {percentage}% on <strong>{quizTitle}</strong>
          </div>
          <div className="mt-3">
            <span className={`badge ${passed ? 'badge-success' : 'badge-warning'}`}>
              {passed ? 'Passed ✓' : 'Below 60%'}
            </span>
          </div>
          <div className="flex gap-1 justify-center mt-3" style={{ flexWrap: 'wrap' }}>
            <button
              className="btn btn-secondary"
              onClick={() => { setResult(null); setAnswers({}); }}
            >
              🔄 Retry Quiz
            </button>
            <button
              className="btn btn-primary"
              onClick={() => navigate('/dashboard')}
            >
              📊 Return to Dashboard →
            </button>
          </div>
        </div>
      </div>
    );
  }

  // --- Quiz Questions ---
  const answeredCount = Object.keys(answers).length;
  const progress = questions.length > 0 ? (answeredCount / questions.length) * 100 : 0;

  return (
    <div className="quiz-container">
      {/* Quiz Header with progress */}
      <div className="quiz-header">
        <h2 style={{ fontFamily: 'var(--font-display)', fontWeight: 700 }}>{quizTitle}</h2>
        <p className="text-muted" style={{ fontSize: '0.875rem', marginTop: '0.25rem' }}>
          {answeredCount} of {questions.length} answered
        </p>
        <div className="quiz-progress-bar">
          <div className="quiz-progress-fill" style={{ width: `${progress}%` }}></div>
        </div>
      </div>

      {/* Error Alert */}
      {error && <AlertMessage type="error" message={error} onClose={() => setError('')} />}

      {/* Question List */}
      {questions.map((q, index) => (
        <QuizQuestion
          key={q.questionId}
          question={q}
          selectedAnswer={answers[q.questionId] || null}
          onSelect={handleSelect}
          questionNumber={index + 1}
          totalQuestions={questions.length}
        />
      ))}

      {/* Submit Button */}
      <button
        id="quiz-submit-btn"
        className="btn btn-primary btn-lg"
        onClick={handleSubmit}
        disabled={submitting}
        style={{ width: '100%' }}
      >
        {submitting ? '⏳ Submitting...' : '📩 Submit Quiz'}
      </button>
    </div>
  );
}

export default Quiz;
