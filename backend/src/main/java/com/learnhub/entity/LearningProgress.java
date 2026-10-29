package com.learnhub.entity;

import jakarta.persistence.*;

/**
 * Entity class representing the 'learning_progress' table.
 * Tracks how far a student has progressed in a course.
 * One record per student-course pair (unique constraint).
 */
@Entity
@Table(
    name = "learning_progress",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_progress",
        columnNames = {"student_id", "course_id"}
    )
)
public class LearningProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // 0 to 100 percent
    @Column(name = "completion_percentage", nullable = false)
    private int completionPercentage = 0;

    // 'IN_PROGRESS' or 'COMPLETED'
    @Column(name = "completion_status", nullable = false, length = 20)
    private String completionStatus = "IN_PROGRESS";

    public LearningProgress() {
    }

    public LearningProgress(Student student, Course course) {
        this.student = student;
        this.course = course;
        this.completionPercentage = 0;
        this.completionStatus = "IN_PROGRESS";
    }

    // ---- Getters and Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public int getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(int completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public String getCompletionStatus() { return completionStatus; }
    public void setCompletionStatus(String completionStatus) {
        this.completionStatus = completionStatus;
    }
}
