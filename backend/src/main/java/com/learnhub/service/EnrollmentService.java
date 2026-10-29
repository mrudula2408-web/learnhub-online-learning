package com.learnhub.service;

import com.learnhub.entity.Course;
import com.learnhub.entity.Enrollment;
import com.learnhub.entity.LearningProgress;
import com.learnhub.entity.Student;
import com.learnhub.repository.CourseRepository;
import com.learnhub.repository.EnrollmentRepository;
import com.learnhub.repository.LearningProgressRepository;
import com.learnhub.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for enrollment business logic.
 * When a student enrolls, we also create an initial LearningProgress record.
 */
@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LearningProgressRepository progressRepository;

    /**
     * Enroll a student in a course.
     * Throws RuntimeException for duplicate enrollment or if course not found.
     * Also creates a LearningProgress record with 0% completion.
     */
    public Enrollment enroll(Long studentId, Long courseId) {
        // Prevent duplicate enrollment
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new RuntimeException("You are already enrolled in this course.");
        }

        // Fetch student and course from the database
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Create and save the enrollment record
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        Enrollment saved = enrollmentRepository.save(enrollment);

        // Also create a LearningProgress record if one doesn't exist
        if (progressRepository.findByStudentIdAndCourseId(studentId, courseId).isEmpty()) {
            LearningProgress progress = new LearningProgress();
            progress.setStudent(student);
            progress.setCourse(course);
            progress.setCompletionPercentage(0);
            progress.setCompletionStatus("IN_PROGRESS");
            progressRepository.save(progress);
        }

        return saved;
    }

    /**
     * Get all enrollments for a student.
     */
    public List<Enrollment> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }
}
