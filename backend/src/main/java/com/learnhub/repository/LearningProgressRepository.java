package com.learnhub.repository;

import com.learnhub.entity.LearningProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for LearningProgress entity.
 */
@Repository
public interface LearningProgressRepository extends JpaRepository<LearningProgress, Long> {

    // Get all progress records for a student
    List<LearningProgress> findByStudentId(Long studentId);

    // Get progress for a specific student-course combination
    Optional<LearningProgress> findByStudentIdAndCourseId(Long studentId, Long courseId);
}
