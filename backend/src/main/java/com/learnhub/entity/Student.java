package com.learnhub.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class representing the 'students' table in MySQL.
 * Each Student object maps to one row in the students table.
 */
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    // Email must be unique — no two students can share the same email
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    // Stores BCrypt hash of the password, never the plain text
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Automatically set the creation timestamp before saving
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // ---- Getters and Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
