package com.learnhub.controller;

import com.learnhub.dto.CourseModulesResponse;
import com.learnhub.service.ModuleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for course learning modules.
 */
@RestController
@RequestMapping("/api")
public class ModuleController {

    private static final String SESSION_STUDENT_ID = "studentId";

    @Autowired
    private ModuleService moduleService;

    // ----------------------------------------------------------------
    // GET /api/courses/{courseId}/modules
    // Fetch the 3 learning modules for a course with completion status
    // ----------------------------------------------------------------
    @GetMapping("/courses/{courseId}/modules")
    public ResponseEntity<?> getCourseModules(@PathVariable Long courseId, HttpSession session) {
        Long studentId = (Long) session.getAttribute(SESSION_STUDENT_ID);
        if (studentId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Please log in to view learning modules.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            CourseModulesResponse result = moduleService.getCourseModules(studentId, courseId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ----------------------------------------------------------------
    // POST /api/modules/{moduleId}/complete
    // Mark a learning module as completed by the student
    // ----------------------------------------------------------------
    @PostMapping("/modules/{moduleId}/complete")
    public ResponseEntity<?> completeModule(@PathVariable Long moduleId, HttpSession session) {
        Long studentId = (Long) session.getAttribute(SESSION_STUDENT_ID);
        if (studentId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Please log in to mark modules completed.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            Map<String, Object> result = moduleService.completeModule(studentId, moduleId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ----------------------------------------------------------------
    // GET /api/modules/summary
    // Get completion summary across all courses for the dashboard
    // ----------------------------------------------------------------
    @GetMapping("/modules/summary")
    public ResponseEntity<?> getModuleSummary(HttpSession session) {
        Long studentId = (Long) session.getAttribute(SESSION_STUDENT_ID);
        if (studentId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Please log in to view progress.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Map<Long, Map<String, Object>> summary = moduleService.getStudentProgressSummary(studentId);
        return ResponseEntity.ok(summary);
    }
}
