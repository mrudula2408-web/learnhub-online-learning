package com.learnhub.controller;

import com.learnhub.entity.Course;
import com.learnhub.entity.Enrollment;
import com.learnhub.service.EnrollmentService;
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
 * REST Controller for course enrollments.
 * All endpoints require the student to be logged in.
 */
@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private static final String SESSION_STUDENT_ID = "studentId";

    @Autowired
    private EnrollmentService enrollmentService;

    // ----------------------------------------------------------------
    // POST /api/enrollments
    // Body: { "courseId": 1 }
    // Enroll the logged-in student in a course
    // ----------------------------------------------------------------
    @PostMapping
    public ResponseEntity<Map<String, Object>> enroll(
            @RequestBody Map<String, Long> body,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();
        Long studentId = (Long) session.getAttribute(SESSION_STUDENT_ID);

        // Check if student is logged in
        if (studentId == null) {
            response.put("success", false);
            response.put("message", "Please log in to enroll in a course.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Long courseId = body.get("courseId");
        if (courseId == null) {
            response.put("success", false);
            response.put("message", "Course ID is required.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Enrollment enrollment = enrollmentService.enroll(studentId, courseId);
            response.put("success", true);
            response.put("message", "Successfully enrolled!");
            response.put("enrollmentId", enrollment.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ----------------------------------------------------------------
    // GET /api/enrollments/my
    // Get all enrollments for the currently logged-in student
    // ----------------------------------------------------------------
    @GetMapping("/my")
    public ResponseEntity<?> getMyEnrollments(HttpSession session) {
        Long studentId = (Long) session.getAttribute(SESSION_STUDENT_ID);

        if (studentId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Please log in to see your enrollments.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(studentId);

        // Map to a simpler JSON format for the frontend
        List<Map<String, Object>> result = enrollments.stream().map(e -> {
            Map<String, Object> item = new HashMap<>();
            item.put("enrollmentId", e.getId());
            item.put("enrolledAt", e.getEnrolledAt());
            Course course = e.getCourse();
            item.put("courseId", course.getId());
            item.put("courseTitle", course.getTitle());
            item.put("instructor", course.getInstructor());
            item.put("duration", course.getDuration());
            item.put("description", course.getDescription());
            return item;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
