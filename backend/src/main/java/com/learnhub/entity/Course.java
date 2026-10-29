package com.learnhub.entity;

import jakarta.persistence.*;

/**
 * Entity class representing the 'courses' table in MySQL.
 */
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    // TEXT type for longer course descriptions
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 100)
    private String instructor;

    // Duration stored as text e.g. "8 hours", "4 weeks"
    @Column(nullable = false, length = 50)
    private String duration;

    public Course() {
    }

    public Course(String title, String description, String instructor, String duration) {
        this.title = title;
        this.description = description;
        this.instructor = instructor;
        this.duration = duration;
    }

    // ---- Getters and Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
}
