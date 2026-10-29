/**
 * QuizQuestion.jsx — Renders a single multiple-choice question.
 * The student clicks an option to select their answer.
 *
 * @param {object}   question       - the question object from the backend
 * @param {string}   selectedAnswer - currently selected answer ('A','B','C','D' or null)
 * @param {function} onSelect       - called with (questionId, answer) when a choice is made
 * @param {number}   questionNumber - display index (1-based)
 * @param {number}   totalQuestions - total for progress display
 */
import React from 'react';

function QuizQuestion({ question, selectedAnswer, onSelect, questionNumber, totalQuestions }) {
  const options = [
    { label: 'A', text: question.optionA },
    { label: 'B', text: question.optionB },
    { label: 'C', text: question.optionC },
    { label: 'D', text: question.optionD },
  ];

  return (
    <div className="question-card" id={`question-${question.questionId}`}>
      {/* Question number */}
      <div className="question-number">
        Question {questionNumber} of {totalQuestions}
      </div>

      {/* Question text */}
      <p className="question-text">{question.questionText}</p>

      {/* Answer options */}
      <div className="options-list" role="radiogroup">
        {options.map(({ label, text }) => (
          <div
            key={label}
            id={`option-${question.questionId}-${label}`}
            className={`option-item ${selectedAnswer === label ? 'selected' : ''}`}
            onClick={() => onSelect(question.questionId, label)}
            role="radio"
            aria-checked={selectedAnswer === label}
            tabIndex={0}
            onKeyDown={(e) => e.key === 'Enter' && onSelect(question.questionId, label)}
          >
            <span className="option-label">{label}</span>
            <span className="option-text">{text}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

export default QuizQuestion;
