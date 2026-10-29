package com.learnhub.controller;

import com.learnhub.dto.QuizQuestionDTO;
import com.learnhub.dto.QuizSubmissionRequest;
import com.learnhub.entity.QuizAttempt;
import com.learnhub.service.QuizService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for quiz operations.
 * Handles fetching questions, server-side scoring, and returning attempt history.
 */
@RestController
@RequestMapping("/api/quizzes")
public class QuizController {

    private static final String SESSION_STUDENT_ID = "studentId";

    @Autowired
    private QuizService quizService;

    @Autowired
    private com.learnhub.service.ModuleService moduleService;

    // ----------------------------------------------------------------
    // GET /api/quizzes/{quizId}/status
    // Check if quiz is unlocked (requires completing all course modules)
    // ----------------------------------------------------------------
    @GetMapping("/{quizId}/status")
    public ResponseEntity<Map<String, Object>> getQuizStatus(
            @PathVariable Long quizId,
            HttpSession session) {
        Long studentId = (Long) session.getAttribute(SESSION_STUDENT_ID);
        Map<String, Object> response = new HashMap<>();

        if (studentId == null) {
            response.put("success", false);
            response.put("message", "Please log in to check quiz status.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        boolean unlocked = moduleService.areAllModulesCompleted(studentId, quizId);
        response.put("quizId", quizId);
        response.put("locked", !unlocked);
        response.put("message", unlocked
                ? "Quiz is unlocked! You can now test your knowledge."
                : "Complete all learning modules to unlock the quiz.");
        return ResponseEntity.ok(response);
    }

    // ----------------------------------------------------------------
    // GET /api/quizzes/course/{courseId}
    // Get available quizzes for a course
    // ----------------------------------------------------------------
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Map<String, Object>>> getQuizzesByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(quizService.getQuizzesForCourse(courseId));
    }

    // ----------------------------------------------------------------
    // GET /api/quizzes/{quizId}/questions
    // Get questions for a quiz (blocked if modules are incomplete)
    // ----------------------------------------------------------------
    @GetMapping("/{quizId}/questions")
    public ResponseEntity<?> getQuizQuestions(@PathVariable Long quizId, HttpSession session) {
        Long studentId = (Long) session.getAttribute(SESSION_STUDENT_ID);
        if (studentId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Please log in to view quiz questions.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        // Enforce backend check: must complete all modules first!
        if (!moduleService.areAllModulesCompleted(studentId, quizId)) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("locked", true);
            error.put("message", "Complete all learning modules to unlock the quiz.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        return ResponseEntity.ok(quizService.getQuestions(quizId));
    }

    // ----------------------------------------------------------------
    // POST /api/quizzes/{quizId}/submit
    // Submit quiz answers - graded on server (blocked if modules are incomplete)
    // ----------------------------------------------------------------
    @PostMapping("/{quizId}/submit")
    public ResponseEntity<Map<String, Object>> submitQuiz(
            @PathVariable Long quizId,
            @RequestBody QuizSubmissionRequest request,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        Long studentId = (Long) session.getAttribute(SESSION_STUDENT_ID);

        if (studentId == null) {
            response.put("success", false);
            response.put("message", "Please log in to submit a quiz.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Backend enforcement: prevent submission if modules not completed
        if (!moduleService.areAllModulesCompleted(studentId, quizId)) {
            response.put("success", false);
            response.put("locked", true);
            response.put("message", "Complete all learning modules to unlock the quiz.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        try {
            QuizAttempt attempt = quizService.submitQuiz(quizId, studentId, request);
            int total = attempt.getTotalQuestions();
            int score = attempt.getScore();
            double pct = total > 0 ? ((double) score / total) * 100 : 0;

            response.put("success", true);
            response.put("attemptId", attempt.getId());
            response.put("quizTitle", attempt.getQuizTitle());
            response.put("score", score);
            response.put("totalQuestions", total);
            response.put("percentage", Math.round(pct));
            response.put("message", "Quiz submitted and score saved successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to submit quiz: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ----------------------------------------------------------------
    // GET /api/quizzes/attempts/my
    // Get quiz attempts for the logged-in student
    // ----------------------------------------------------------------
    @GetMapping("/attempts/my")
    public ResponseEntity<?> getMyAttempts(HttpSession session) {
        Long studentId = (Long) session.getAttribute(SESSION_STUDENT_ID);

        if (studentId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please log in to view your attempts."));
        }

        List<QuizAttempt> attempts = quizService.getStudentAttempts(studentId);
        List<Map<String, Object>> result = attempts.stream().map(a -> {
            Map<String, Object> item = new HashMap<>();
            item.put("attemptId", a.getId());
            item.put("quizTitle", a.getQuizTitle());
            item.put("score", a.getScore());
            item.put("totalQuestions", a.getTotalQuestions());
            item.put("attemptDate", a.getAttemptDate().toString());
            return item;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
