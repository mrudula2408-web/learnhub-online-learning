/**
 * HomePage.jsx — The landing page of LearnHub.
 * Shows a hero section and a preview of available courses.
 */
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import CourseCard from '../components/CourseCard.jsx';
import courseService from '../services/courseService.js';

function HomePage({ student }) {
  const navigate = useNavigate();
  const [featuredCourses, setFeaturedCourses] = useState([]);

  // Fetch a few courses to show on the home page
  useEffect(() => {
    courseService.getCourses().then(data => {
      setFeaturedCourses(data.slice(0, 3)); // Show first 3 courses
    }).catch(() => {});
  }, []);

  return (
    <div>
      {/* ---- Hero Section ---- */}
      <section className="hero">
        <div className="hero-badge">
          <span>🚀</span> Online Learning Platform
        </div>

        <h1>
          Learn Smarter with<br />
          <span className="gradient-text">LearnHub</span>
        </h1>

        <p className="hero-description">
          Access high-quality courses, take interactive quizzes, and track your
          learning progress — all in one place.
        </p>

        <div className="hero-actions">
          <button
            id="hero-browse-courses-btn"
            className="btn btn-primary btn-lg"
            onClick={() => navigate('/courses')}
          >
            Browse Courses →
          </button>
          {!student ? (
            <button
              id="hero-register-btn"
              className="btn btn-secondary btn-lg"
              onClick={() => navigate('/register')}
            >
              Register Free
            </button>
          ) : (
            <button
              id="hero-dashboard-btn"
              className="btn btn-secondary btn-lg"
              onClick={() => navigate('/dashboard')}
            >
              My Dashboard
            </button>
          )}
        </div>

        {/* Stats */}
        <div className="hero-stats">
          <div className="stat-item">
            <h3>5+</h3>
            <p>Expert Courses</p>
          </div>
          <div className="stat-item">
            <h3>2</h3>
            <p>Interactive Quizzes</p>
          </div>
          <div className="stat-item">
            <h3>100%</h3>
            <p>Progress Tracking</p>
          </div>
          <div className="stat-item">
            <h3>Free</h3>
            <p>Registration</p>
          </div>
        </div>
      </section>

      {/* ---- Featured Courses ---- */}
      {featuredCourses.length > 0 && (
        <section style={{ padding: '0 1.5rem 4rem', maxWidth: '1200px', margin: '0 auto' }}>
          <h2 className="section-title">
            🔥 Featured Courses
          </h2>
          <div className="course-grid">
            {featuredCourses.map((course, index) => (
              <CourseCard
                key={course.id}
                course={course}
                index={index}
                onViewDetails={() => navigate('/courses')}
              />
            ))}
          </div>
          <div className="text-center mt-3">
            <button
              className="btn btn-secondary"
              onClick={() => navigate('/courses')}
            >
              View All Courses →
            </button>
          </div>
        </section>
      )}

      {/* ---- Features Section ---- */}
      <section style={{
        padding: '3rem 1.5rem',
        background: 'rgba(255,255,255,0.02)',
        borderTop: '1px solid var(--color-border)',
        borderBottom: '1px solid var(--color-border)',
      }}>
        <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
          <h2 className="section-title text-center" style={{ justifyContent: 'center', marginBottom: '2.5rem' }}>
            ✨ Why Choose LearnHub?
          </h2>
          <div className="course-grid">
            {[
              { icon: '🎓', title: 'Expert-Led Courses', desc: 'Learn from experienced instructors with real-world knowledge in various technology domains.' },
              { icon: '🧪', title: 'Interactive Quizzes', desc: 'Test your understanding with multiple-choice quizzes. Scores are calculated securely on the server.' },
              { icon: '📊', title: 'Track Your Progress', desc: 'Mark lessons complete and monitor your course completion percentage from your personal dashboard.' },
            ].map((f, i) => (
              <div key={i} className="card" style={{ textAlign: 'center' }}>
                <div style={{ fontSize: '2.5rem', marginBottom: '1rem' }}>{f.icon}</div>
                <h3 style={{ fontFamily: 'var(--font-display)', fontWeight: 700, marginBottom: '0.5rem' }}>{f.title}</h3>
                <p className="text-muted" style={{ fontSize: '0.875rem' }}>{f.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>
    </div>
  );
}

export default HomePage;
