/**
 * ProgressCard.jsx — Displays learning progress for a single course.
 * Shows a progress bar and a dropdown to update the completion percentage.
 *
 * @param {object}   progress   - progress object from the backend
 * @param {function} onUpdate   - called with (courseId, percentage, status) to save changes
 */
import React, { useState } from 'react';

function ProgressCard({ progress, onUpdate }) {
  const [updating, setUpdating] = useState(false);
  const [localPercentage, setLocalPercentage] = useState(progress.completionPercentage);

  const handleUpdate = async (newPercentage) => {
    setLocalPercentage(newPercentage);
    setUpdating(true);
    try {
      const status = newPercentage >= 100 ? 'COMPLETED' : 'IN_PROGRESS';
      await onUpdate(progress.courseId, newPercentage, status);
    } finally {
      setUpdating(false);
    }
  };

  const isCompleted = progress.completionStatus === 'COMPLETED';
  const pct = localPercentage;

  return (
    <div className="progress-card" id={`progress-card-${progress.courseId}`}>
      {/* Course title and status badge */}
      <div className="flex items-center justify-between">
        <h4 style={{ fontWeight: 600, fontSize: '0.95rem', flex: 1 }}>
          {progress.courseTitle}
        </h4>
        <span className={`badge ${isCompleted ? 'badge-success' : 'badge-primary'}`}>
          {isCompleted ? '✓ Completed' : 'In Progress'}
        </span>
      </div>

      {/* Progress Bar */}
      <div className="progress-bar-track">
        <div
          className="progress-bar-fill"
          style={{
            width: `${pct}%`,
            background: isCompleted
              ? 'linear-gradient(90deg, #10b981, #059669)'
              : 'linear-gradient(90deg, #6366f1, #818cf8)',
          }}
        ></div>
      </div>

      {/* Percentage and update controls */}
      <div className="flex items-center justify-between mt-1" style={{ gap: '1rem' }}>
        <span className="text-muted" style={{ fontSize: '0.8rem' }}>
          {pct}% complete
        </span>

        {/* Quick update buttons */}
        {!isCompleted && (
          <div className="flex gap-1 flex-wrap">
            {[25, 50, 75, 100].map(val => (
              <button
                key={val}
                id={`progress-update-${progress.courseId}-${val}`}
                className="btn btn-sm btn-secondary"
                style={{ padding: '0.25rem 0.6rem', fontSize: '0.75rem' }}
                onClick={() => handleUpdate(val)}
                disabled={updating || pct >= val}
              >
                {val === 100 ? 'Complete ✓' : `${val}%`}
              </button>
            ))}
          </div>
        )}

        {isCompleted && (
          <span style={{ color: 'var(--color-success)', fontSize: '0.8rem', fontWeight: 600 }}>
            🎉 Course Completed!
          </span>
        )}
      </div>
    </div>
  );
}

export default ProgressCard;
