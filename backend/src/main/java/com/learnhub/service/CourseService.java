package com.learnhub.service;

import com.learnhub.entity.Course;
import com.learnhub.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for course-related business logic.
 */
@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    /**
     * Return all courses in the database.
     */
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    /**
     * Search courses by title keyword.
     * If keyword is blank, return all courses.
     */
    public List<Course> searchCourses(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return courseRepository.findAll();
        }
        return courseRepository.findByTitleContainingIgnoreCase(keyword);
    }

    /**
     * Find a single course by its ID.
     */
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }
}
