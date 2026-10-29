package com.learnhub;

import com.learnhub.entity.Course;
import com.learnhub.repository.CourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Main entry point for the LearnHub Spring Boot application.
 * Starts embedded Tomcat server on port 8081 and initializes JPA.
 */
@SpringBootApplication
public class LearnHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearnHubApplication.class, args);
    }

    /**
     * Seeds initial sample courses safely if the courses table is empty.
     * Never duplicates existing courses.
     */
    @Bean
    public CommandLineRunner initData(CourseRepository courseRepository) {
        return args -> {
            if (courseRepository.count() == 0) {
                courseRepository.saveAll(List.of(
                    new Course("Introduction to Java Programming",
                        "Learn fundamental Java concepts including variables, loops, object-oriented programming (OOP), classes, inheritance, and methods. Perfect for beginners.",
                        "Dr. Ravi Kumar", "10 hours"),
                    new Course("Web Development with React",
                        "Build modern web user interfaces using React JS components, state hooks, props, and connecting to Spring Boot REST APIs.",
                        "Prof. Anita Sharma", "12 hours"),
                    new Course("Database Management with MySQL",
                        "Learn relational databases, SQL queries (SELECT, INSERT, UPDATE, DELETE), primary/foreign keys, joins, and JDBC connectivity.",
                        "Dr. Suresh Patel", "8 hours"),
                    new Course("Data Structures and Algorithms",
                        "Master core DSA concepts in Java: Arrays, Linked Lists, Stacks, Queues, Trees, Searching, and Sorting algorithms with time complexity.",
                        "Prof. Deepa Nair", "14 hours"),
                    new Course("Operating Systems Fundamentals",
                        "Core principles of modern operating systems: Processes, CPU scheduling, Memory management, Virtual memory, Deadlocks, and File systems.",
                        "Dr. Arjun Mehta", "9 hours")
                ));
            }
        };
    }
}
