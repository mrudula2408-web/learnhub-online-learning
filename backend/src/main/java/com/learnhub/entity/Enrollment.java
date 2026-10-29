package com.learnhub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class representing the 'enrollments' table.
 * Links a Student to a Course. A student can only enroll once per course.
 */
@Entity
@Table(
    name = "enrollments",
    // Unique constraint prevents duplicate enrollments in the database
    uniqueConstraints = @UniqueConstraint(
        name = "uq_student_course",
        columnNames = {"student_id", "course_id"}
    )
)
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many enrollments can belong to one student
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Many enrollments can belong to one course
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "enrolled_at", nullable = false)
    private LocalDateTime enrolledAt;

    @PrePersist
    public void prePersist() {
        this.enrolledAt = LocalDateTime.now();
    }

    // ---- Getters and Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(LocalDateTime enrolledAt) { this.enrolledAt = enrolledAt; }
}
