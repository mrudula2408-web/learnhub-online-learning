-- =============================================================
-- LearnHub Database Script (MySQL 8)
-- Compatible with MySQL Workbench
--
-- EXACTLY 5 BASIC TABLES (as required for B.Tech CSE evaluation):
-- 1. students          - student registration & login details
-- 2. courses           - available courses
-- 3. enrollments       - student course enrollments (no duplicates)
-- 4. quiz_attempts     - quiz scores saved per student
-- 5. learning_progress - course completion progress
-- =============================================================

-- Step 1: Create Database
CREATE DATABASE IF NOT EXISTS learnhub_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Step 2: Use Database
USE learnhub_db;

-- -------------------------------------------------------------
-- 1. TABLE: students
-- Stores registered students with BCrypt hashed passwords
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS students (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100)  NOT NULL,
    email         VARCHAR(150)  NOT NULL UNIQUE,
    password_hash VARCHAR(255)  NOT NULL,
    created_at    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- -------------------------------------------------------------
-- 2. TABLE: courses
-- Stores available courses displayed on the Home page
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS courses (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(200)  NOT NULL,
    description TEXT          NOT NULL,
    instructor  VARCHAR(100)  NOT NULL,
    duration    VARCHAR(50)   NOT NULL DEFAULT '8 hours'
);

-- -------------------------------------------------------------
-- 3. TABLE: enrollments
-- Tracks student enrollment in courses
-- UNIQUE constraint prevents duplicate enrollment
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS enrollments (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id  BIGINT   NOT NULL,
    course_id   BIGINT   NOT NULL,
    enrolled_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_enroll_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_enroll_course  FOREIGN KEY (course_id)  REFERENCES courses(id)  ON DELETE CASCADE,
    CONSTRAINT uq_student_course UNIQUE (student_id, course_id)
);

-- -------------------------------------------------------------
-- 4. TABLE: quiz_attempts
-- Saves the student's score calculated by the Java backend
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS quiz_attempts (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id      BIGINT       NOT NULL,
    course_id       BIGINT       NOT NULL,
    quiz_title      VARCHAR(200) NOT NULL,
    score           INT          NOT NULL DEFAULT 0,
    total_questions INT          NOT NULL DEFAULT 5,
    attempt_date    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attempt_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_attempt_course  FOREIGN KEY (course_id)  REFERENCES courses(id)  ON DELETE CASCADE
);

-- -------------------------------------------------------------
-- 5. TABLE: learning_progress
-- Tracks completion percentage (0-100%) and status
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS learning_progress (
    id                    BIGINT      AUTO_INCREMENT PRIMARY KEY,
    student_id            BIGINT      NOT NULL,
    course_id             BIGINT      NOT NULL,
    completion_percentage INT         NOT NULL DEFAULT 0,
    completion_status     VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    last_updated          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_progress_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_progress_course  FOREIGN KEY (course_id)  REFERENCES courses(id)  ON DELETE CASCADE,
    CONSTRAINT uq_progress UNIQUE (student_id, course_id)
);

-- -------------------------------------------------------------
-- SAMPLE DATA: Initial Courses (INSERT IGNORE prevents duplicates)
-- -------------------------------------------------------------
INSERT IGNORE INTO courses (id, title, description, instructor, duration) VALUES
(1, 'Introduction to Java Programming',
 'Learn fundamental Java concepts including variables, loops, object-oriented programming (OOP), classes, inheritance, and methods. Perfect for beginners.',
 'Dr. Ravi Kumar', '10 hours'),

(2, 'Web Development with React',
 'Build modern web user interfaces using React JS components, state hooks, props, and connecting to Spring Boot REST APIs.',
 'Prof. Anita Sharma', '12 hours'),

(3, 'Database Management with MySQL',
 'Learn relational databases, SQL queries (SELECT, INSERT, UPDATE, DELETE), primary/foreign keys, joins, and JDBC connectivity.',
 'Dr. Suresh Patel', '8 hours'),

(4, 'Data Structures and Algorithms',
 'Master core DSA concepts in Java: Arrays, Linked Lists, Stacks, Queues, Trees, Searching, and Sorting algorithms with time complexity.',
 'Prof. Deepa Nair', '14 hours'),

(5, 'Operating Systems Fundamentals',
 'Core principles of modern operating systems: Processes, CPU scheduling, Memory management, Virtual memory, Deadlocks, and File systems.',
 'Dr. Arjun Mehta', '9 hours');
