package com.learnhub.repository;

import com.learnhub.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Enrollment entity.
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // Get all enrollments for a specific student
    List<Enrollment> findByStudentId(Long studentId);

    // Check if a student is already enrolled in a course
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
}
