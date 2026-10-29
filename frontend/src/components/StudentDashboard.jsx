/**
 * StudentDashboard.jsx — The student's personal dashboard.
 * Displays enrolled courses, module completion progress, and quiz results.
 * All data is fetched from the Spring Boot backend.
 */
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import AlertMessage from './AlertMessage.jsx';
import enrollmentService from '../services/enrollmentService.js';
import quizService from '../services/quizService.js';
import progressService from '../services/progressService.js';
import moduleService from '../services/moduleService.js';

function StudentDashboard({ student }) {
  const navigate = useNavigate();

  // Tab state: 'courses' | 'progress' | 'quizzes'
  const [activeTab, setActiveTab] = useState('courses');

  // Data fetched from backend
  const [enrollments, setEnrollments] = useState([]);
  const [attempts, setAttempts] = useState([]);
  const [progressList, setProgressList] = useState([]);
  const [moduleSummary, setModuleSummary] = useState({});

  // UI state
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState({ type: '', text: '' });

  // Fetch all dashboard data on mount
  const fetchAll = async () => {
    setLoading(true);
    try {
      const [enrollData, attemptData, progressData, summaryData] = await Promise.all([
        enrollmentService.getMyEnrollments(),
        quizService.getMyAttempts(),
        progressService.getMyProgress(),
        moduleService.getModuleSummary().catch(() => ({})),
      ]);
      setEnrollments(enrollData || []);
      setAttempts(attemptData || []);
      setProgressList(progressData || []);
      setModuleSummary(summaryData || {});
    } catch (err) {
      setMessage({ type: 'error', text: 'Failed to load dashboard data.' });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAll();
  }, []);

  // Get initials for avatar
  const initials = student?.name
    ? student.name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2)
    : '?';

  if (loading) {
    return <div className="loading-spinner"><div className="spinner"></div></div>;
  }

  return (
    <div>
      {/* Profile Header */}
      <div className="dashboard-header">
        <div className="dashboard-avatar">{initials}</div>
        <div className="dashboard-welcome">
          <h2>Welcome back, {student?.name}! 👋</h2>
          <p className="text-muted">{student?.email}</p>
          <div className="flex flex-wrap gap-1 mt-2">
            <span className="badge badge-primary">📚 {enrollments.length} Enrolled Course{enrollments.length !== 1 ? 's' : ''}</span>
            <span className="badge badge-primary">🎯 {attempts.length} Quiz Attempt{attempts.length !== 1 ? 's' : ''}</span>
          </div>
        </div>
      </div>

      {/* Alert Message */}
      {message.text && (
        <AlertMessage
          type={message.type}
          message={message.text}
          onClose={() => setMessage({ type: '', text: '' })}
        />
      )}

      {/* Dashboard Tabs */}
      <div className="dashboard-tabs">
        {[
          { key: 'courses', label: '📚 My Courses' },
          { key: 'progress', label: '📈 Learning Progress' },
          { key: 'quizzes', label: '🎯 Quiz Results' },
        ].map(tab => (
          <button
            key={tab.key}
            id={`dashboard-tab-${tab.key}`}
            className={`tab-btn ${activeTab === tab.key ? 'active' : ''}`}
            onClick={() => setActiveTab(tab.key)}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* Tab: Enrolled Courses */}
      {activeTab === 'courses' && (
        <div>
          {enrollments.length === 0 ? (
            <div className="empty-state">
              <div className="empty-state-icon">📭</div>
              <h3>No courses enrolled yet</h3>
              <p>Browse available courses and enroll to start learning.</p>
              <button
                className="btn btn-primary mt-2"
                onClick={() => navigate('/courses')}
              >
                Browse Courses
              </button>
            </div>
          ) : (
            <div className="grid-2">
              {enrollments.map(enroll => {
                const summary = moduleSummary[enroll.courseId] || {};
                const completed = summary.completedModules || 0;
                const total = summary.totalModules || 3;
                const pct = summary.percentage || 0;
                const quizUnlocked = summary.quizUnlocked || false;
                const latestAttempt = attempts.find(a => a.courseId === enroll.courseId);

                return (
                  <div key={enroll.enrollmentId} className="card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
                    <div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: '0.5rem', marginBottom: '0.5rem' }}>
                        <h3 style={{ fontFamily: 'var(--font-display)', fontWeight: 700, fontSize: '1.1rem', margin: 0 }}>
                          {enroll.courseTitle}
                        </h3>
                        <span className="badge badge-primary" style={{ fontSize: '0.75rem', whiteSpace: 'nowrap' }}>
                          ⏱️ {enroll.duration}
                        </span>
                      </div>

                      <p className="text-muted" style={{ fontSize: '0.85rem', marginBottom: '0.8rem' }}>
                        👨‍🏫 Instructor: {enroll.instructor}
                      </p>

                      {/* Module Completion Status */}
                      <div style={{ backgroundColor: 'rgba(255, 255, 255, 0.03)', padding: '0.75rem 1rem', borderRadius: '8px', marginBottom: '1rem', border: '1px solid var(--color-border)' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.85rem', marginBottom: '0.4rem' }}>
                          <span style={{ fontWeight: 600 }}>📖 Modules Completed:</span>
                          <span style={{ fontWeight: 'bold', color: 'var(--color-primary-light)' }}>
                            {completed} of {total} ({pct}%)
                          </span>
                        </div>
                        <div className="progress-bar-container" style={{ height: '6px' }}>
                          <div
                            className="progress-bar-fill"
                            style={{
                              width: `${pct}%`,
                              background: pct === 100
                                ? 'var(--color-success)'
                                : 'linear-gradient(90deg, var(--color-primary), var(--color-accent))'
                            }}
                          ></div>
                        </div>

                        {/* Quiz Status Badge */}
                        <div style={{ marginTop: '0.6rem', fontSize: '0.8rem', display: 'flex', alignItems: 'center', gap: '0.3rem' }}>
                          {latestAttempt ? (
                            <span className="badge badge-success" style={{ fontSize: '0.75rem' }}>
                              🏆 Score: {latestAttempt.score}/{latestAttempt.totalQuestions || 5}
                            </span>
                          ) : quizUnlocked ? (
                            <span className="badge badge-success" style={{ fontSize: '0.75rem' }}>
                              🔓 Quiz Unlocked
                            </span>
                          ) : (
                            <span className="badge badge-secondary" style={{ fontSize: '0.75rem', opacity: 0.8 }}>
                              🔒 Quiz Locked (complete all 3 modules)
                            </span>
                          )}
                        </div>
                      </div>
                    </div>

                    {/* Action buttons */}
                    <div style={{ display: 'flex', gap: '0.5rem', flexWrap: 'wrap', marginTop: '0.5rem' }}>
                      <button
                        className="btn btn-primary btn-sm"
                        onClick={() => navigate(`/learn/${enroll.courseId}`)}
                        style={{ flex: 1 }}
                      >
                        🚀 Continue Learning →
                      </button>

                      {quizUnlocked && (
                        <button
                          className="btn btn-success btn-sm"
                          onClick={() => navigate(`/quiz/${enroll.courseId}`)}
                        >
                          📝 Take Quiz
                        </button>
                      )}
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      )}

      {/* Tab: Learning Progress */}
      {activeTab === 'progress' && (
        <div>
          {enrollments.length === 0 ? (
            <div className="empty-state">
              <div className="empty-state-icon">📊</div>
              <h3>No courses enrolled yet</h3>
              <p>Enroll in a course to view and track your module progress.</p>
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1.2rem' }}>
              {enrollments.map(enroll => {
                const summary = moduleSummary[enroll.courseId] || {};
                const completed = summary.completedModules || 0;
                const total = summary.totalModules || 3;
                const pct = summary.percentage || 0;
                const quizUnlocked = summary.quizUnlocked || false;
                const latestAttempt = attempts.find(a => a.courseId === enroll.courseId);

                return (
                  <div key={enroll.enrollmentId} className="card" style={{ padding: '1.5rem' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.8rem', flexWrap: 'wrap', gap: '0.5rem' }}>
                      <div>
                        <h3 style={{ fontSize: '1.15rem', margin: 0 }}>{enroll.courseTitle}</h3>
                        <p className="text-muted" style={{ fontSize: '0.85rem', margin: '0.2rem 0 0 0' }}>
                          Instructor: {enroll.instructor}
                        </p>
                      </div>

                      <div style={{ textAlign: 'right' }}>
                        <span style={{ fontSize: '1.25rem', fontWeight: 'bold', color: 'var(--color-primary-light)' }}>
                          {pct}%
                        </span>
                        <div style={{ fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>
                          {completed} of {total} modules done
                        </div>
                      </div>
                    </div>

                    <div className="progress-bar-container" style={{ height: '8px', marginBottom: '1rem' }}>
                      <div
                        className="progress-bar-fill"
                        style={{
                          width: `${pct}%`,
                          background: pct === 100
                            ? 'var(--color-success)'
                            : 'linear-gradient(90deg, var(--color-primary), var(--color-accent))'
                        }}
                      ></div>
                    </div>

                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '0.5rem' }}>
                      <div style={{ fontSize: '0.85rem' }}>
                        {latestAttempt ? (
                          <span style={{ color: 'var(--color-success)', fontWeight: 600 }}>
                            ✅ Quiz Completed with score: {latestAttempt.score}/{latestAttempt.totalQuestions || 5}
                          </span>
                        ) : quizUnlocked ? (
                          <span style={{ color: 'var(--color-success)', fontWeight: 600 }}>
                            🔓 All 3 modules complete! Quiz is ready.
                          </span>
                        ) : (
                          <span style={{ color: 'var(--color-text-muted)' }}>
                            🔒 Complete remaining {total - completed} module{total - completed !== 1 ? 's' : ''} to unlock quiz.
                          </span>
                        )}
                      </div>

                      <button
                        className="btn btn-secondary btn-sm"
                        onClick={() => navigate(`/learn/${enroll.courseId}`)}
                      >
                        Open Learning Modules →
                      </button>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      )}

      {/* Tab: Quiz Results */}
      {activeTab === 'quizzes' && (
        <div>
          {attempts.length === 0 ? (
            <div className="empty-state">
              <div className="empty-state-icon">🎯</div>
              <h3>No quiz attempts yet</h3>
              <p>Complete all 3 learning modules for any course to unlock and take the quiz.</p>
            </div>
          ) : (
            <div style={{ overflowX: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                  <tr style={{ borderBottom: '1px solid var(--color-border)' }}>
                    {['Quiz', 'Score', 'Status', 'Date'].map(h => (
                      <th key={h} style={{
                        textAlign: 'left',
                        padding: '0.75rem 1rem',
                        color: 'var(--color-text-muted)',
                        fontSize: '0.8rem',
                        textTransform: 'uppercase',
                        letterSpacing: '0.05em',
                      }}>{h}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {attempts.map(a => {
                    const total = a.totalQuestions || 5;
                    const passed = a.score >= Math.ceil(total * 0.6);
                    return (
                      <tr
                        key={a.attemptId}
                        style={{ borderBottom: '1px solid var(--color-border)' }}
                      >
                        <td style={{ padding: '0.85rem 1rem', fontWeight: 500 }}>
                          {a.quizTitle}
                        </td>
                        <td style={{ padding: '0.85rem 1rem' }}>
                          <span className={`badge ${passed ? 'badge-success' : 'badge-warning'}`}>
                            {a.score} / {total} ({Math.round((a.score / total) * 100)}%)
                          </span>
                        </td>
                        <td style={{ padding: '0.85rem 1rem' }}>
                          <span style={{ color: passed ? 'var(--color-success)' : 'var(--color-warning)', fontWeight: 600, fontSize: '0.85rem' }}>
                            {passed ? '✓ PASSED' : 'RETRY NEEDED'}
                          </span>
                        </td>
                        <td style={{ padding: '0.85rem 1rem', color: 'var(--color-text-muted)', fontSize: '0.85rem' }}>
                          {new Date(a.attemptDate).toLocaleDateString('en-IN', {
                            year: 'numeric', month: 'short', day: 'numeric',
                            hour: '2-digit', minute: '2-digit',
                          })}
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default StudentDashboard;
