/**
 * LearningPage.jsx — Course Learning Page with 3 sequential modules
 * Students read topics, study external textbook/tutorial links,
 * mark modules as completed, and unlock the final course quiz.
 */
import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import AlertMessage from '../components/AlertMessage.jsx';
import moduleService from '../services/moduleService.js';

function LearningPage({ student }) {
  const { courseId } = useParams();
  const navigate = useNavigate();

  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [completingId, setCompletingId] = useState(null);
  const [message, setMessage] = useState({ type: '', text: '' });

  // Fetch course modules on load
  const loadModules = async () => {
    try {
      setLoading(true);
      const res = await moduleService.getCourseModules(courseId);
      setData(res);
    } catch (err) {
      setMessage({ type: 'error', text: err.message || 'Failed to load course modules.' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadModules();
  }, [courseId]);

  // Handle Mark as Completed
  const handleComplete = async (moduleId) => {
    try {
      setCompletingId(moduleId);
      const res = await moduleService.completeModule(moduleId);
      setMessage({ type: 'success', text: res.message || 'Module marked as completed!' });
      // Reload module completion state
      await loadModules();
    } catch (err) {
      setMessage({ type: 'error', text: err.message || 'Failed to complete module.' });
    } finally {
      setCompletingId(null);
    }
  };

  if (loading && !data) {
    return (
      <div className="page">
        <div className="container" style={{ textAlign: 'center', paddingTop: '4rem' }}>
          <div className="spinner"></div>
          <p style={{ marginTop: '1rem', color: 'var(--color-text-secondary)' }}>Loading learning modules...</p>
        </div>
      </div>
    );
  }

  const {
    courseTitle = 'Course Learning',
    instructor = '',
    totalModules = 3,
    completedCount = 0,
    progressPercentage = 0,
    quizUnlocked = false,
    modules = [],
  } = data || {};

  return (
    <div className="page">
      <div className="container" style={{ maxWidth: '900px' }}>
        {/* Navigation Breadcrumb */}
        <div style={{ marginBottom: '1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <button className="btn btn-secondary btn-sm" onClick={() => navigate('/courses')}>
            ← Back to Courses
          </button>
          <button className="btn btn-secondary btn-sm" onClick={() => navigate('/dashboard')}>
            Student Dashboard →
          </button>
        </div>

        {/* Course Header Banner */}
        <div className="card" style={{ padding: '2rem', marginBottom: '2rem', borderLeft: '5px solid var(--color-primary)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem' }}>
            <div>
              <span className="badge badge-primary" style={{ marginBottom: '0.5rem', display: 'inline-block' }}>
                🎓 Course Learning Path
              </span>
              <h1 style={{ fontSize: '1.8rem', margin: '0.3rem 0 0.5rem 0' }}>{courseTitle}</h1>
              <p style={{ color: 'var(--color-text-secondary)', margin: 0 }}>
                Instructor: <strong>{instructor}</strong> &bull; Complete all {totalModules} modules to unlock your assessment quiz.
              </p>
            </div>

            {/* Progress summary pill */}
            <div style={{ textAlign: 'right', minWidth: '160px' }}>
              <div style={{ fontSize: '1.4rem', fontWeight: 'bold', color: 'var(--color-primary-light)' }}>
                {progressPercentage}%
              </div>
              <div style={{ fontSize: '0.85rem', color: 'var(--color-text-secondary)' }}>
                {completedCount} of {totalModules} completed
              </div>
            </div>
          </div>

          {/* Overall Course Progress Bar */}
          <div className="progress-bar-container" style={{ marginTop: '1.2rem', height: '10px' }}>
            <div
              className="progress-bar-fill"
              style={{
                width: `${progressPercentage}%`,
                background: progressPercentage === 100
                  ? 'var(--color-success)'
                  : 'linear-gradient(90deg, var(--color-primary), var(--color-accent))'
              }}
            ></div>
          </div>
        </div>

        {/* Alert feedback */}
        {message.text && (
          <AlertMessage
            type={message.type}
            message={message.text}
            onClose={() => setMessage({ type: '', text: '' })}
          />
        )}

        {/* Modules List */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem', marginBottom: '2.5rem' }}>
          {modules.map((mod, index) => {
            const isCompleted = mod.completed;
            return (
              <div
                key={mod.id}
                className="card"
                style={{
                  padding: '1.8rem',
                  border: isCompleted ? '1px solid rgba(16, 185, 129, 0.4)' : '1px solid var(--color-border)',
                  backgroundColor: isCompleted ? 'rgba(16, 185, 129, 0.03)' : 'var(--color-surface)',
                  position: 'relative',
                  transition: 'all 0.2s ease',
                }}
              >
                {/* Module Header */}
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.8rem', flexWrap: 'wrap', gap: '0.5rem' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                    <span
                      style={{
                        display: 'inline-flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        width: '32px',
                        height: '32px',
                        borderRadius: '50%',
                        backgroundColor: isCompleted ? 'var(--color-success)' : 'var(--color-primary)',
                        color: '#fff',
                        fontWeight: 'bold',
                        fontSize: '0.9rem',
                      }}
                    >
                      {isCompleted ? '✓' : mod.moduleNumber}
                    </span>
                    <h2 style={{ fontSize: '1.25rem', margin: 0 }}>{mod.title}</h2>
                  </div>

                  {isCompleted ? (
                    <span className="badge badge-success" style={{ padding: '0.35rem 0.75rem', fontSize: '0.8rem' }}>
                      ✓ Completed
                    </span>
                  ) : (
                    <span className="badge badge-warning" style={{ padding: '0.35rem 0.75rem', fontSize: '0.8rem' }}>
                      ⏳ In Progress
                    </span>
                  )}
                </div>

                {/* Module Description */}
                <p style={{ color: 'var(--color-text-secondary)', lineHeight: 1.6, marginBottom: '1.2rem' }}>
                  {mod.description}
                </p>

                {/* External Learning Resources */}
                <div
                  style={{
                    backgroundColor: 'rgba(255, 255, 255, 0.03)',
                    border: '1px solid var(--color-border)',
                    borderRadius: '8px',
                    padding: '1rem',
                    marginBottom: '1.2rem',
                  }}
                >
                  <div style={{ fontSize: '0.85rem', fontWeight: 600, color: 'var(--color-primary-light)', marginBottom: '0.6rem', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                    <span>📚</span> Recommended Learning &amp; Tutorial Resources:
                    <span style={{ fontSize: '0.75rem', color: 'var(--color-text-muted)', fontWeight: 'normal' }}>
                      (Opens official textbook / tutorial in new tab)
                    </span>
                  </div>

                  <ul style={{ listStyle: 'none', padding: 0, margin: 0, display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                    {mod.link1Url && (
                      <li>
                        <a
                          href={mod.link1Url}
                          target="_blank"
                          rel="noopener noreferrer"
                          style={{
                            color: 'var(--color-accent)',
                            textDecoration: 'none',
                            fontWeight: 500,
                            display: 'inline-flex',
                            alignItems: 'center',
                            gap: '0.3rem',
                            fontSize: '0.9rem',
                          }}
                          onMouseOver={(e) => (e.target.style.textDecoration = 'underline')}
                          onMouseOut={(e) => (e.target.style.textDecoration = 'none')}
                        >
                          🌐 {mod.link1Title || 'Official Learning Resource'} <span style={{ fontSize: '0.75rem' }}>↗</span>
                        </a>
                      </li>
                    )}

                    {mod.link2Url && (
                      <li>
                        <a
                          href={mod.link2Url}
                          target="_blank"
                          rel="noopener noreferrer"
                          style={{
                            color: 'var(--color-accent)',
                            textDecoration: 'none',
                            fontWeight: 500,
                            display: 'inline-flex',
                            alignItems: 'center',
                            gap: '0.3rem',
                            fontSize: '0.9rem',
                          }}
                          onMouseOver={(e) => (e.target.style.textDecoration = 'underline')}
                          onMouseOut={(e) => (e.target.style.textDecoration = 'none')}
                        >
                          🌐 {mod.link2Title || 'Documentation / Reference'} <span style={{ fontSize: '0.75rem' }}>↗</span>
                        </a>
                      </li>
                    )}
                  </ul>
                </div>

                {/* Completion Action */}
                <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                  {isCompleted ? (
                    <button className="btn btn-secondary btn-sm" disabled style={{ opacity: 0.8, cursor: 'default' }}>
                      ✓ Module Completed
                    </button>
                  ) : (
                    <button
                      className="btn btn-primary btn-sm"
                      onClick={() => handleComplete(mod.id)}
                      disabled={completingId === mod.id}
                    >
                      {completingId === mod.id ? 'Saving...' : '✓ Mark as Completed'}
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </div>

        {/* Final Quiz Unlock Card */}
        <div
          className="card"
          style={{
            padding: '2rem',
            border: quizUnlocked ? '2px solid var(--color-success)' : '1px dashed var(--color-border)',
            backgroundColor: quizUnlocked ? 'rgba(16, 185, 129, 0.05)' : 'rgba(255, 255, 255, 0.02)',
            textAlign: 'center',
          }}
        >
          <div style={{ fontSize: '2.5rem', marginBottom: '0.5rem' }}>
            {quizUnlocked ? '🔓' : '🔒'}
          </div>

          <h3 style={{ fontSize: '1.4rem', margin: '0.3rem 0 0.5rem 0' }}>
            {quizUnlocked ? 'Course Quiz Unlocked!' : 'Course Assessment Quiz'}
          </h3>

          <p style={{ color: 'var(--color-text-secondary)', maxWidth: '600px', margin: '0 auto 1.5rem auto' }}>
            {quizUnlocked
              ? 'Congratulations! You have completed all course modules. You are now eligible to take the course assessment quiz. Your score will be evaluated by the Java backend and saved in MySQL.'
              : `Complete all learning modules above to unlock the quiz (${completedCount} of ${totalModules} completed so far).`}
          </p>

          <div>
            {quizUnlocked ? (
              <button
                className="btn btn-success btn-lg"
                onClick={() => navigate(`/quiz/${courseId}`)}
                style={{ padding: '0.8rem 2rem', fontSize: '1.05rem', fontWeight: 'bold' }}
              >
                📝 Take Course Quiz →
              </button>
            ) : (
              <button
                className="btn btn-secondary btn-lg"
                disabled
                style={{ opacity: 0.6, cursor: 'not-allowed', padding: '0.8rem 2rem' }}
              >
                🔒 Complete All Modules to Unlock
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default LearningPage;
