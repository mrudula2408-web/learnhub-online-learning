package com.learnhub.controller;

import com.learnhub.dto.LoginRequest;
import com.learnhub.dto.RegisterRequest;
import com.learnhub.entity.Student;
import com.learnhub.service.StudentService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for authentication — register, login, logout, get current user.
 *
 * Spring MVC concepts used here:
 * - @RestController: marks this class as a controller that returns JSON
 * - @RequestMapping: base URL for all methods in this class
 * - @PostMapping / @GetMapping: maps HTTP POST/GET to specific methods
 * - @RequestBody: reads JSON from the request body
 * - @Valid: triggers validation annotations in the DTO
 * - HttpSession: server-side session for storing logged-in student's ID
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Session attribute key for storing the logged-in student's ID
    private static final String SESSION_STUDENT_ID = "studentId";

    @Autowired
    private StudentService studentService;

    // ----------------------------------------------------------------
    // POST /api/auth/register
    // Register a new student account
    // ----------------------------------------------------------------
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @Valid @RequestBody RegisterRequest request,
            BindingResult bindingResult) {

        Map<String, Object> response = new HashMap<>();

        // Return validation errors if input is invalid
        if (bindingResult.hasErrors()) {
            response.put("success", false);
            response.put("message", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Student student = studentService.register(request);
            response.put("success", true);
            response.put("message", "Registration successful! You can now log in.");
            response.put("studentId", student.getId());
            response.put("name", student.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // ----------------------------------------------------------------
    // POST /api/auth/login
    // Validate credentials and store student ID in session
    // ----------------------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody LoginRequest request,
            BindingResult bindingResult,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            response.put("success", false);
            response.put("message", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return ResponseEntity.badRequest().body(response);
        }

        // Validate credentials using StudentService
        Student student = studentService.login(request.getEmail(), request.getPassword());

        if (student == null) {
            response.put("success", false);
            response.put("message", "Invalid email or password.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Store student ID in the server-side HTTP session
        session.setAttribute(SESSION_STUDENT_ID, student.getId());

        response.put("success", true);
        response.put("message", "Login successful!");
        response.put("studentId", student.getId());
        response.put("name", student.getName());
        response.put("email", student.getEmail());
        return ResponseEntity.ok(response);
    }

    // ----------------------------------------------------------------
    // POST /api/auth/logout
    // Invalidate the session (log out the student)
    // ----------------------------------------------------------------
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate(); // Remove all session attributes
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logged out successfully.");
        return ResponseEntity.ok(response);
    }

    // ----------------------------------------------------------------
    // GET /api/auth/me
    // Return the currently logged-in student's info
    // ----------------------------------------------------------------
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentStudent(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        Long studentId = (Long) session.getAttribute(SESSION_STUDENT_ID);
        if (studentId == null) {
            response.put("success", false);
            response.put("message", "Not logged in.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        return studentService.findById(studentId).map(student -> {
            response.put("success", true);
            response.put("studentId", student.getId());
            response.put("name", student.getName());
            response.put("email", student.getEmail());
            // Never include passwordHash in the response!
            return ResponseEntity.ok(response);
        }).orElseGet(() -> {
            response.put("success", false);
            response.put("message", "Student not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        });
    }
}
