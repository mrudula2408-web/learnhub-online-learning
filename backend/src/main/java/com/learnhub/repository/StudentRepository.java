package com.learnhub.repository;

import com.learnhub.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Student entity.
 * Spring Data JPA auto-generates the implementation at runtime.
 * JpaRepository<Student, Long> gives us save(), findById(), findAll(), delete() etc.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // Find a student by email — used during login and duplicate-check
    Optional<Student> findByEmail(String email);

    // Check if an email is already registered
    boolean existsByEmail(String email);
}
