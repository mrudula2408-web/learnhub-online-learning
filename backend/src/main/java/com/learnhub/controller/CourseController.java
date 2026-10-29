package com.learnhub.controller;

import com.learnhub.entity.Course;
import com.learnhub.service.CourseService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for course listings and search.
 * Protected: requires an active student session on the backend.
 */
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private static final String SESSION_STUDENT_ID = "studentId";

    @Autowired
    private CourseService courseService;

    // ----------------------------------------------------------------
    // GET /api/courses
    // GET /api/courses?search=java  (search by title)
    // Protected: Requires authenticated student session
    // ----------------------------------------------------------------
    @GetMapping
    public ResponseEntity<?> getCourses(
            @RequestParam(required = false) String search,
            HttpSession session) {

        if (session.getAttribute(SESSION_STUDENT_ID) == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Please log in to view courses.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        List<Course> courses = courseService.searchCourses(search);
        return ResponseEntity.ok(courses);
    }

    // ----------------------------------------------------------------
    // GET /api/courses/{id}
    // Get details of a single course
    // ----------------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute(SESSION_STUDENT_ID) == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Please log in to view course details.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Optional<Course> course = courseService.getCourseById(id);
        return course
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
