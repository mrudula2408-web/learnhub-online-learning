package com.learnhub.repository;

import com.learnhub.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository for the 'quiz_attempts' table.
 */
@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    // Find all quiz attempts for a specific student, newest first
    List<QuizAttempt> findByStudentIdOrderByAttemptDateDesc(Long studentId);

    // Find attempts by student and course
    List<QuizAttempt> findByStudentIdAndCourseId(Long studentId, Long courseId);
}
