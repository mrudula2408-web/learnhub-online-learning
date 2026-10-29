package com.learnhub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class representing the 'quiz_attempts' table.
 * Stores each quiz submission along with the score calculated by the backend.
 */
@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Student who attempted the quiz
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Course associated with this quiz
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // Title of the quiz taken (e.g. "Java Basics Quiz")
    @Column(name = "quiz_title", nullable = false)
    private String quizTitle;

    // Score awarded by backend scoring logic
    @Column(nullable = false)
    private int score;

    // Total number of questions in the quiz
    @Column(name = "total_questions", nullable = false)
    private int totalQuestions = 5;

    // Timestamp when quiz was submitted
    @Column(name = "attempt_date", nullable = false)
    private LocalDateTime attemptDate;

    public QuizAttempt() {
    }

    public QuizAttempt(Student student, Course course, String quizTitle, int score, int totalQuestions) {
        this.student = student;
        this.course = course;
        this.quizTitle = quizTitle;
        this.score = score;
        this.totalQuestions = totalQuestions;
    }

    @PrePersist
    public void prePersist() {
        if (this.attemptDate == null) {
            this.attemptDate = LocalDateTime.now();
        }
    }

    // ---- Getters and Setters ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public LocalDateTime getAttemptDate() {
        return attemptDate;
    }

    public void setAttemptDate(LocalDateTime attemptDate) {
        this.attemptDate = attemptDate;
    }
}
