package com.learnhub.controller;

import com.learnhub.dto.ProgressUpdateRequest;
import com.learnhub.entity.LearningProgress;
import com.learnhub.service.ProgressService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for learning progress operations.
 */
@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private static final String SESSION_STUDENT_ID = "studentId";

    @Autowired
    private ProgressService progressService;

    // ----------------------------------------------------------------
    // GET /api/progress/my
    // Get all progress records for the logged-in student
    // ----------------------------------------------------------------
    @GetMapping("/my")
    public ResponseEntity<?> getMyProgress(HttpSession session) {
        Long studentId = (Long) session.getAttribute(SESSION_STUDENT_ID);

        if (studentId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Please log in to see your progress.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        List<LearningProgress> progressList = progressService.getProgressByStudent(studentId);

        List<Map<String, Object>> result = progressList.stream().map(p -> {
            Map<String, Object> item = new HashMap<>();
            item.put("progressId", p.getId());
            item.put("courseId", p.getCourse().getId());
            item.put("courseTitle", p.getCourse().getTitle());
            item.put("completionPercentage", p.getCompletionPercentage());
            item.put("completionStatus", p.getCompletionStatus());
            return item;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // ----------------------------------------------------------------
    // PUT /api/progress/{courseId}
    // Update progress for the logged-in student in a specific course
    // Body: { "completionPercentage": 50, "completionStatus": "IN_PROGRESS" }
    // ----------------------------------------------------------------
    @PutMapping("/{courseId}")
    public ResponseEntity<Map<String, Object>> updateProgress(
            @PathVariable Long courseId,
            @Valid @RequestBody ProgressUpdateRequest request,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        Long studentId = (Long) session.getAttribute(SESSION_STUDENT_ID);

        if (studentId == null) {
            response.put("success", false);
            response.put("message", "Please log in to update your progress.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            LearningProgress progress = progressService.updateProgress(studentId, courseId, request);
            response.put("success", true);
            response.put("message", "Progress updated successfully!");
            response.put("progressId", progress.getId());
            response.put("completionPercentage", progress.getCompletionPercentage());
            response.put("completionStatus", progress.getCompletionStatus());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
