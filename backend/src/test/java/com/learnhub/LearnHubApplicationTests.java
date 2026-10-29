package com.learnhub;

import com.learnhub.entity.Student;
import com.learnhub.repository.StudentRepository;
import com.learnhub.service.StudentService;
import com.learnhub.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic integration tests for the LearnHub backend.
 *
 * NOTE: These tests require a running MySQL database.
 * Configure test database credentials in application-test.properties
 * or set environment variables before running tests.
 *
 * To run: .\mvnw.cmd test (from the backend directory)
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class LearnHubApplicationTests {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * Test that the Spring application context loads successfully.
     * If this fails, there is a configuration or dependency error.
     */
    @Test
    void contextLoads() {
        // This test passes if the application context starts without errors
        assertNotNull(studentService);
        assertNotNull(studentRepository);
    }

    /**
     * Test that BCryptPasswordEncoder correctly hashes and verifies passwords.
     */
    @Test
    void testPasswordHashing() {
        String rawPassword = "mySecretPassword123";
        String hash = passwordEncoder.encode(rawPassword);

        // Hash should not equal the plain text
        assertNotEquals(rawPassword, hash);

        // BCrypt verify should return true for correct password
        assertTrue(passwordEncoder.matches(rawPassword, hash));

        // BCrypt verify should return false for wrong password
        assertFalse(passwordEncoder.matches("wrongPassword", hash));
    }

    /**
     * Test student registration stores a hashed password.
     */
    @Test
    void testStudentRegistration() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Test Student");
        req.setEmail("test.student.unique@example.com");
        req.setPassword("password123");

        Student saved = studentService.register(req);

        assertNotNull(saved.getId());
        assertEquals("Test Student", saved.getName());
        assertEquals("test.student.unique@example.com", saved.getEmail());
        // Password must be stored as a BCrypt hash, not plain text
        assertNotEquals("password123", saved.getPasswordHash());
        assertTrue(saved.getPasswordHash().startsWith("$2a$"));

        // Cleanup
        studentRepository.deleteById(saved.getId());
    }

    /**
     * Test that duplicate email registration throws an exception.
     */
    @Test
    void testDuplicateEmailThrowsException() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Duplicate User");
        req.setEmail("duplicate.unique@example.com");
        req.setPassword("password123");

        Student saved = studentService.register(req);

        // Try registering again with the same email
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            studentService.register(req);
        });

        assertTrue(exception.getMessage().contains("already registered"));

        // Cleanup
        studentRepository.deleteById(saved.getId());
    }
}
