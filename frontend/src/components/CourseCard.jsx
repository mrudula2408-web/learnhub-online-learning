/**
 * CourseCard.jsx — Reusable card component for displaying a single course.
 * Shows course title, instructor, duration, and description.
 * Accepts optional action buttons as children.
 */
import React from 'react';
import { useNavigate } from 'react-router-dom';

// Map course index to an emoji icon for visual variety
const COURSE_ICONS = ['💻', '🌐', '🗄️', '🐍', '⚙️', '📊', '🔒', '📱'];

function CourseCard({ course, index = 0, onViewDetails, onEnroll, isEnrolled, children }) {
  const navigate = useNavigate();
  const icon = COURSE_ICONS[index % COURSE_ICONS.length];

  return (
    <div className="course-card" id={`course-card-${course.id}`}>
      {/* Colorful top header with icon */}
      <div className="course-card-header" style={{
        background: `linear-gradient(135deg, hsl(${(index * 47) % 360}, 65%, 35%), hsl(${(index * 47 + 60) % 360}, 70%, 45%))`
      }}>
        <span style={{ position: 'relative', zIndex: 1, fontSize: '2.5rem' }}>{icon}</span>
      </div>

      {/* Card body */}
      <div className="course-card-body">
        <h2 className="course-title">{course.title}</h2>
        <p className="course-description">{course.description}</p>

        {/* Meta info: instructor and duration */}
        <div className="course-meta">
          <span className="course-meta-item">
            👨‍🏫 {course.instructor}
          </span>
          <span className="course-meta-item">
            ⏱️ {course.duration}
          </span>
        </div>
      </div>

      {/* Card footer with action buttons */}
      <div className="course-card-footer">
        {/* View Details button */}
        {onViewDetails && (
          <button
            id={`view-course-${course.id}`}
            className="btn btn-secondary btn-sm"
            onClick={() => onViewDetails(course)}
          >
            View Details
          </button>
        )}

        {/* If enrolled, show Start Learning button; otherwise Enroll Now */}
        {isEnrolled ? (
          <button
            id={`start-learning-${course.id}`}
            className="btn btn-primary btn-sm"
            onClick={() => navigate(`/learn/${course.id}`)}
          >
            🚀 Start Learning →
          </button>
        ) : (
          onEnroll && (
            <button
              id={`enroll-course-${course.id}`}
              className="btn btn-primary btn-sm"
              onClick={() => onEnroll(course.id)}
            >
              Enroll Now
            </button>
          )
        )}

        {/* Any extra buttons passed as children */}
        {children}
      </div>
    </div>
  );
}

export default CourseCard;
