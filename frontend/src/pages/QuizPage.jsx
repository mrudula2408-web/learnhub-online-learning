/**
 * QuizPage.jsx — Quiz page for a specific course.
 * Enforces module completion: quiz is locked until all modules are completed.
 */
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Quiz from '../components/Quiz.jsx';
import AlertMessage from '../components/AlertMessage.jsx';
import quizService from '../services/quizService.js';
import moduleService from '../services/moduleService.js';

function QuizPage({ student }) {
  const { courseId } = useParams();
  const navigate = useNavigate();

  const [quizzes, setQuizzes] = useState([]);
  const [selectedQuiz, setSelectedQuiz] = useState(null);
  const [lockStatus, setLockStatus] = useState({ locked: false, message: '' });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!student) {
      navigate('/login');
      return;
    }

    const loadData = async () => {
      setLoading(true);
      try {
        // 1. Check lock status first
        const status = await moduleService.getQuizStatus(courseId);
        setLockStatus(status);

        // 2. If unlocked, load quizzes
        if (!status.locked) {
          const data = await quizService.getQuizzesByCourse(courseId);
          setQuizzes(data);
        }
      } catch (err) {
        setError(err.message || 'Failed to check quiz status.');
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [courseId, student, navigate]);

  if (!student) return null;

  if (loading) {
    return <div className="loading-spinner"><div className="spinner"></div></div>;
  }

  return (
    <div className="page">
      <div className="container" style={{ maxWidth: '800px' }}>
        {/* Back button */}
        <div style={{ marginBottom: '1.5rem', display: 'flex', gap: '0.75rem' }}>
          <button
            id="quiz-back-btn"
            className="btn btn-secondary btn-sm"
            onClick={() => selectedQuiz ? setSelectedQuiz(null) : navigate(`/learn/${courseId}`)}
          >
            ← Back to Learning Modules
          </button>
          <button
            className="btn btn-secondary btn-sm"
            onClick={() => navigate('/dashboard')}
          >
            Dashboard
          </button>
        </div>

        {error && <AlertMessage type="error" message={error} />}

        {/* LOCKED STATE */}
        {lockStatus.locked ? (
          <div className="card" style={{ padding: '3rem 2rem', textAlign: 'center', border: '1px dashed var(--color-border)' }}>
            <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>🔒</div>
            <h2 style={{ fontSize: '1.6rem', marginBottom: '0.75rem' }}>Quiz Locked</h2>
            <p style={{ color: 'var(--color-text-secondary)', maxWidth: '500px', margin: '0 auto 1.5rem auto' }}>
              {lockStatus.message || 'You must complete all 3 learning modules for this course before you can take the assessment quiz.'}
            </p>
            <button
              className="btn btn-primary"
              onClick={() => navigate(`/learn/${courseId}`)}
              style={{ padding: '0.75rem 1.75rem' }}
            >
              📖 Go to Learning Modules →
            </button>
          </div>
        ) : selectedQuiz ? (
          /* ACTIVE QUIZ COMPONENT */
          <Quiz
            quizId={selectedQuiz.quizId}
            quizTitle={selectedQuiz.title}
            onFinish={() => {
              // Option to return to dashboard after finish
            }}
          />
        ) : (
          /* UNLOCKED: SELECT QUIZ */
          <div>
            <div className="page-header" style={{ textAlign: 'left', marginBottom: '1.5rem' }}>
              <span className="badge badge-success" style={{ marginBottom: '0.5rem', display: 'inline-block' }}>
                🔓 All Modules Completed &bull; Quiz Unlocked
              </span>
              <h1 style={{ margin: '0.3rem 0' }}>🎯 Course Assessment Quiz</h1>
              <p style={{ color: 'var(--color-text-secondary)' }}>
                Test your knowledge. Your score is graded on the Java backend and saved in MySQL.
              </p>
            </div>

            {quizzes.length === 0 ? (
              <div className="empty-state">
                <div className="empty-state-icon">📝</div>
                <h3>No quizzes available</h3>
                <p>There are no quizzes configured for this course yet.</p>
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                {quizzes.map(quiz => (
                  <div key={quiz.quizId} className="card" style={{ padding: '1.8rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem' }}>
                    <div>
                      <h3 style={{ fontFamily: 'var(--font-display)', fontWeight: 700, margin: '0 0 0.4rem 0' }}>
                        {quiz.title}
                      </h3>
                      <p className="text-muted" style={{ margin: 0, fontSize: '0.875rem' }}>
                        5 multiple-choice questions &bull; Passing score: 60% (3/5)
                      </p>
                    </div>
                    <button
                      id={`start-quiz-${quiz.quizId}`}
                      className="btn btn-success"
                      onClick={() => setSelectedQuiz(quiz)}
                    >
                      Start Quiz ➔
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}

export default QuizPage;
