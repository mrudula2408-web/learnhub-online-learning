/**
 * AlertMessage.jsx — Reusable alert/notification component
 * Displays success, error, info, or warning messages.
 */
import React from 'react';

const ICONS = {
  success: '✓',
  error:   '✕',
  info:    'ℹ',
  warning: '⚠',
};

/**
 * @param {string} type    - 'success' | 'error' | 'info' | 'warning'
 * @param {string} message - The message to display
 * @param {function} onClose - Optional close handler
 */
function AlertMessage({ type = 'info', message, onClose }) {
  if (!message) return null;

  return (
    <div className={`alert alert-${type}`} role="alert">
      <span className="alert-icon">{ICONS[type]}</span>
      <span>{typeof message === 'object' ? (message.msg || message.message || JSON.stringify(message)) : message}</span>
      {onClose && (
        <button
          onClick={onClose}
          style={{
            marginLeft: 'auto',
            background: 'none',
            border: 'none',
            cursor: 'pointer',
            color: 'inherit',
            fontSize: '1rem',
            opacity: 0.7,
          }}
          aria-label="Close alert"
        >
          ✕
        </button>
      )}
    </div>
  );
}

export default AlertMessage;
