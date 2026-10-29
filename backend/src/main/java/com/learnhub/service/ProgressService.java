package com.learnhub.service;

import com.learnhub.dto.ProgressUpdateRequest;
import com.learnhub.entity.Course;
import com.learnhub.entity.LearningProgress;
import com.learnhub.entity.Student;
import com.learnhub.repository.CourseRepository;
import com.learnhub.repository.LearningProgressRepository;
import com.learnhub.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for learning progress business logic.
 */
@Service
public class ProgressService {

    @Autowired
    private LearningProgressRepository progressRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    /**
     * Get all progress records for a student.
     */
    public List<LearningProgress> getProgressByStudent(Long studentId) {
        return progressRepository.findByStudentId(studentId);
    }

    /**
     * Update a student's progress for a specific course.
     * If no record exists yet, creates one.
     */
    public LearningProgress updateProgress(Long studentId, Long courseId, ProgressUpdateRequest request) {
        // Try to find existing progress record
        Optional<LearningProgress> existingOpt =
                progressRepository.findByStudentIdAndCourseId(studentId, courseId);

        LearningProgress progress;
        if (existingOpt.isPresent()) {
            // Update existing record
            progress = existingOpt.get();
        } else {
            // Create new record if it doesn't exist
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));
            progress = new LearningProgress();
            progress.setStudent(student);
            progress.setCourse(course);
        }

        progress.setCompletionPercentage(request.getCompletionPercentage());
        progress.setCompletionStatus(request.getCompletionStatus());

        // If 100%, automatically mark as COMPLETED
        if (request.getCompletionPercentage() >= 100) {
            progress.setCompletionStatus("COMPLETED");
        }

        return progressRepository.save(progress);
    }
}
