/**
 * CoursesPage.jsx — Browse and search all available courses.
 * Allows logged-in students to enroll directly from this page.
 */
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import CourseList from '../components/CourseList.jsx';
import AlertMessage from '../components/AlertMessage.jsx';
import enrollmentService from '../services/enrollmentService.js';

function CoursesPage({ student }) {
  const navigate = useNavigate();
  const [enrolledIds, setEnrolledIds] = useState([]);     // Course IDs the student is enrolled in
  const [message, setMessage] = useState({ type: '', text: '' });

  // Fetch current enrollments so we know which courses are already enrolled
  useEffect(() => {
    if (student) {
      enrollmentService.getMyEnrollments()
        .then(data => setEnrolledIds(data.map(e => e.courseId)))
        .catch(() => {});
    }
  }, [student]);

  // Handle enrollment when student clicks "Enroll Now"
  const handleEnroll = async (courseId) => {
    if (!student) {
      navigate('/login');
      return;
    }
    try {
      await enrollmentService.enroll(courseId);
      setEnrolledIds(prev => [...prev, courseId]);
      setMessage({ type: 'success', text: '🎉 Successfully enrolled in the course!' });
    } catch (err) {
      setMessage({ type: 'error', text: err.message || 'Enrollment failed.' });
    }
  };

  return (
    <div className="page">
      <div className="container">
        {/* Page Header */}
        <div className="page-header">
          <h1>📚 Available Courses</h1>
          <p>Explore our courses and start learning today.</p>
        </div>

        {/* Enrollment success/error message */}
        {message.text && (
          <AlertMessage
            type={message.type}
            message={message.text}
            onClose={() => setMessage({ type: '', text: '' })}
          />
        )}

        {/* Course List with search */}
        <CourseList
          student={student}
          enrolledCourseIds={enrolledIds}
          onEnroll={handleEnroll}
          onViewDetails={(course) => {
            // Scroll to course detail or navigate (simple: show alert for now)
            setMessage({ type: 'info', text: `Course: ${course.title} | Instructor: ${course.instructor} | Duration: ${course.duration}` });
          }}
        />
      </div>
    </div>
  );
}

export default CoursesPage;
