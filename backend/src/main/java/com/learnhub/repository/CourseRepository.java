package com.learnhub.repository;

import com.learnhub.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Course entity.
 * Provides basic CRUD and a search method.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // Search courses by title (case-insensitive, partial match)
    // Spring Data generates the SQL: WHERE LOWER(title) LIKE LOWER('%keyword%')
    List<Course> findByTitleContainingIgnoreCase(String title);
}
