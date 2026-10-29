package com.learnhub.dto;

import java.util.Map;

/**
 * Data Transfer Object for quiz submission.
 * The student sends: quizId and a map of { questionId -> selectedAnswer }
 * Example: { "answers": { "1": "B", "2": "C", "3": "A" } }
 */
public class QuizSubmissionRequest {

    // Key = question ID (as string), Value = selected answer ("A", "B", "C", or "D")
    private Map<Long, String> answers;

    public Map<Long, String> getAnswers() { return answers; }
    public void setAnswers(Map<Long, String> answers) { this.answers = answers; }
}
