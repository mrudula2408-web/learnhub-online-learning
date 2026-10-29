/**
 * CourseList.jsx — Displays a searchable list of CourseCard components.
 * Fetches courses from the backend and handles search.
 */
import React, { useState, useEffect } from 'react';
import CourseCard from './CourseCard.jsx';
import AlertMessage from './AlertMessage.jsx';
import courseService from '../services/courseService.js';

function CourseList({ student, enrolledCourseIds = [], onEnroll, onViewDetails }) {
  const [courses, setCourses] = useState([]);       // All fetched courses
  const [searchTerm, setSearchTerm] = useState(''); // Search input value
  const [loading, setLoading] = useState(true);     // Loading state
  const [error, setError] = useState('');

  // Fetch courses from the backend whenever searchTerm changes
  useEffect(() => {
    const fetchCourses = async () => {
      setLoading(true);
      setError('');
      try {
        const data = await courseService.getCourses(searchTerm);
        setCourses(Array.isArray(data) ? data : []);
      } catch (err) {
        setError(err.message || 'Failed to load courses. Please ensure the backend is running.');
      } finally {
        setLoading(false);
      }
    };

    // Debounce: wait 400ms after user stops typing before fetching
    const timer = setTimeout(fetchCourses, 400);
    return () => clearTimeout(timer);
  }, [searchTerm]);

  return (
    <div>
      {/* Search Bar */}
      <div className="search-bar">
        <input
          id="course-search-input"
          type="text"
          className="form-input"
          placeholder="🔍 Search courses by title..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        {searchTerm && (
          <button
            className="btn btn-secondary btn-sm"
            onClick={() => setSearchTerm('')}
          >
            Clear
          </button>
        )}
      </div>

      {/* Error Alert */}
      {error && <AlertMessage type="error" message={error} />}

      {/* Loading Spinner */}
      {loading && (
        <div className="loading-spinner">
          <div className="spinner"></div>
        </div>
      )}

      {/* Empty State */}
      {!loading && courses.length === 0 && !error && (
        <div className="empty-state">
          <div className="empty-state-icon">🔍</div>
          <h3>No courses found</h3>
          <p>Try a different search term or clear the search.</p>
        </div>
      )}

      {/* Course Grid */}
      {!loading && courses.length > 0 && (
        <div className="course-grid">
          {courses.map((course, index) => (
            <CourseCard
              key={course.id}
              course={course}
              index={index}
              isEnrolled={enrolledCourseIds.includes(course.id)}
              onViewDetails={onViewDetails}
              onEnroll={student ? onEnroll : null}
            />
          ))}
        </div>
      )}

      {/* Prompt to login if not logged in */}
      {!student && !loading && courses.length > 0 && (
        <p className="text-center text-muted mt-3">
          <a href="/login">Log in</a> or <a href="/register">register</a> to enroll in courses.
        </p>
      )}
    </div>
  );
}

export default CourseList;
