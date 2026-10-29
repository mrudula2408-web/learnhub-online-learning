package com.learnhub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity tracking module completion per student.
 * UNIQUE(student_id, module_id) prevents duplicate completion credits.
 */
@Entity
@Table(
    name = "module_completions",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_student_module",
        columnNames = {"student_id", "module_id"}
    )
)
public class ModuleCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private CourseModule module;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    public ModuleCompletion() {
    }

    public ModuleCompletion(Student student, CourseModule module, Course course) {
        this.student = student;
        this.module = module;
        this.course = course;
    }

    @PrePersist
    public void prePersist() {
        if (this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }

    // ---- Getters and Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public CourseModule getModule() { return module; }
    public void setModule(CourseModule module) { this.module = module; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
