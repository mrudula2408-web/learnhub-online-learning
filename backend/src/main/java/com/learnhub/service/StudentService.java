package com.learnhub.service;

import com.learnhub.dto.RegisterRequest;
import com.learnhub.entity.Student;
import com.learnhub.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for student-related business logic.
 * The @Service annotation marks this as a Spring-managed component.
 * Controllers call service methods; services call repositories.
 */
@Service
public class StudentService {

    // Spring injects these dependencies automatically
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Register a new student.
     * Hashes the password before storing in the database.
     * Throws RuntimeException if email is already taken.
     */
    public Student register(RegisterRequest request) {
        // Check for duplicate email
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered. Please use a different email.");
        }

        // Create a new Student entity
        Student student = new Student();
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        // Hash the password using BCrypt before saving
        student.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // Save to database and return the saved entity
        return studentRepository.save(student);
    }

    /**
     * Validate login credentials.
     * Returns the Student if credentials are correct, or null otherwise.
     */
    public Student login(String email, String password) {
        // Look up student by email
        Optional<Student> studentOpt = studentRepository.findByEmail(email);
        if (studentOpt.isEmpty()) {
            return null; // No student with this email
        }

        Student student = studentOpt.get();

        // BCrypt compare: checks plain-text password against stored hash
        if (passwordEncoder.matches(password, student.getPasswordHash())) {
            return student; // Credentials are correct
        }

        return null; // Password does not match
    }

    /**
     * Find a student by their ID.
     */
    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }
}
