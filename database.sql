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
-- 6. TABLE: course_modules
-- 3 learning modules per course with verified educational links
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS course_modules (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id     BIGINT       NOT NULL,
    module_number INT          NOT NULL,
    title         VARCHAR(200) NOT NULL,
    description   TEXT         NOT NULL,
    link1_title   VARCHAR(200),
    link1_url     VARCHAR(500),
    link2_title   VARCHAR(200),
    link2_url     VARCHAR(500),
    CONSTRAINT fk_module_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT uq_course_module UNIQUE (course_id, module_number)
);

-- -------------------------------------------------------------
-- 7. TABLE: module_completions
-- Tracks which modules a student has completed
-- UNIQUE(student_id, module_id) prevents duplicate completion
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS module_completions (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id   BIGINT   NOT NULL,
    module_id    BIGINT   NOT NULL,
    course_id    BIGINT   NOT NULL,
    completed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_completion_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_completion_module  FOREIGN KEY (module_id)  REFERENCES course_modules(id) ON DELETE CASCADE,
    CONSTRAINT fk_completion_course  FOREIGN KEY (course_id)  REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT uq_student_module UNIQUE (student_id, module_id)
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

-- -------------------------------------------------------------
-- SAMPLE DATA: Course Learning Modules (3 per course)
-- -------------------------------------------------------------
INSERT IGNORE INTO course_modules (id, course_id, module_number, title, description, link1_title, link1_url, link2_title, link2_url) VALUES
-- Course 1: Java
(1, 1, 1, 'Module 1: Java Basics', 'Introduction to Java syntax, JVM, JRE, JDK, bytecode execution, and writing your first Java program.', 'Official Java Learning Portal (dev.java)', 'https://dev.java/learn/', 'Oracle Java Tutorials', 'https://docs.oracle.com/javase/tutorial/'),
(2, 1, 2, 'Module 2: Variables and Control Statements', 'Primitive data types, variable scopes, arithmetic/logical operators, if-else decision making, and loop iterations.', 'W3Schools Java Variables & Conditions', 'https://www.w3schools.com/java/java_variables.asp', 'GeeksforGeeks Control Statements', 'https://www.geeksforgeeks.org/decision-making-javaif-else-switch-break-continue-jump/'),
(3, 1, 3, 'Module 3: Object-Oriented Programming', 'Classes, objects, constructors, inheritance, polymorphism, abstraction, and encapsulation in Java.', 'Oracle OOP Concepts Guide', 'https://docs.oracle.com/javase/tutorial/java/concepts/', 'JavaTpoint Java OOPs Concepts', 'https://www.javatpoint.com/java-oops-concepts'),

-- Course 2: React
(4, 2, 1, 'Module 1: React Fundamentals & JSX', 'Understanding the React component architecture, JSX syntax, Virtual DOM, and rendering elements.', 'React.dev Official Quick Start', 'https://react.dev/learn', 'MDN Getting Started with React', 'https://developer.mozilla.org/en-US/docs/Learn/Tools_and_testing/Client-side_JavaScript_frameworks/React_getting_started'),
(5, 2, 2, 'Module 2: Components, Props, and State', 'Functional components, passing data using immutable props, and managing reactive local state with useState.', 'React.dev State as a Component''s Memory', 'https://react.dev/learn/state-a-components-memory', 'W3Schools React Props & State', 'https://www.w3schools.com/react/react_props.asp'),
(6, 2, 3, 'Module 3: Side Effects & REST API Integration', 'Managing component lifecycle with useEffect, asynchronous fetch calls to Spring Boot REST APIs, and error handling.', 'React.dev Synchronizing with Effects', 'https://react.dev/learn/synchronizing-with-effects', 'FreeCodeCamp Fetch API in React', 'https://www.freecodecamp.org/news/how-to-fetch-api-data-in-react/'),

-- Course 3: MySQL
(7, 3, 1, 'Module 1: Relational Database Concepts & SQL Basics', 'Fundamentals of relational databases, schema design, primary and foreign keys, and MySQL Workbench essentials.', 'MySQL Official Reference Manual', 'https://dev.mysql.com/doc/refman/8.0/en/', 'W3Schools SQL Introduction', 'https://www.w3schools.com/sql/sql_intro.asp'),
(8, 3, 2, 'Module 2: CRUD Operations & Querying', 'Writing efficient SQL queries using SELECT, INSERT, UPDATE, DELETE, WHERE filtering, and aggregation functions.', 'SQL Tutorial - SQL Basics', 'https://www.sqltutorial.org/sql-basic/', 'MySQL Query Syntax Reference', 'https://dev.mysql.com/doc/refman/8.0/en/sql-syntax.html'),
(9, 3, 3, 'Module 3: Joins, Constraints & Indexing', 'Combining tables with INNER JOIN, LEFT JOIN, database normalization (1NF, 2NF, 3NF), and foreign key constraints.', 'W3Schools SQL Joins Tutorial', 'https://www.w3schools.com/sql/sql_join.asp', 'GeeksforGeeks Database Normalization', 'https://www.geeksforgeeks.org/introduction-of-database-normalization/'),

-- Course 4: DSA
(10, 4, 1, 'Module 1: Complexity Analysis & Linear Data Structures', 'Big-O asymptotic notation, time/space complexity, static arrays, dynamic lists, and Singly/Doubly Linked Lists.', 'GeeksforGeeks Data Structures Guide', 'https://www.geeksforgeeks.org/data-structures/', 'Programiz DSA Linear Structures', 'https://www.programiz.com/dsa'),
(11, 4, 2, 'Module 2: Stacks, Queues & Hashing', 'Stack LIFO operations, Queue FIFO implementations, circular queues, hashing techniques, and Java HashMap.', 'GeeksforGeeks Stack & Queue Guide', 'https://www.geeksforgeeks.org/stack-data-structure/', 'Baeldung Guide to Java HashMap', 'https://www.baeldung.com/java-hashmap'),
(12, 4, 3, 'Module 3: Trees, Graphs & Sorting Algorithms', 'Binary search trees, tree traversals (inorder, preorder, postorder), binary search, merge sort, and quick sort.', 'GeeksforGeeks Binary Search Tree', 'https://www.geeksforgeeks.org/binary-search-tree-data-structure/', 'Programiz Sorting Algorithms Tutorial', 'https://www.programiz.com/dsa/sorting-algorithm'),

-- Course 5: OS
(13, 5, 1, 'Module 1: Processes, Threads & CPU Scheduling', 'Process states, process control blocks (PCB), context switching, multithreading, and CPU scheduling algorithms.', 'GeeksforGeeks Operating Systems Tutorial', 'https://www.geeksforgeeks.org/operating-systems/', 'TutorialsPoint OS Process Management', 'https://www.tutorialspoint.com/operating_system/os_processes.htm'),
(14, 5, 2, 'Module 2: Process Synchronization & Deadlocks', 'Race conditions, critical sections, semaphores, deadlock conditions, deadlock prevention, and Banker''s Algorithm.', 'GeeksforGeeks Process Synchronization', 'https://www.geeksforgeeks.org/process-synchronization-in-operating-system/', 'JavaTpoint OS Deadlocks Introduction', 'https://www.javatpoint.com/os-deadlocks-introduction'),
(15, 5, 3, 'Module 3: Memory Management & File Systems', 'Contiguous memory allocation, paging, segmentation, virtual memory concepts, and page replacement algorithms.', 'GeeksforGeeks Memory Management in OS', 'https://www.geeksforgeeks.org/memory-management-in-operating-system/', 'TutorialsPoint Virtual Memory in OS', 'https://www.tutorialspoint.com/operating_system/os_virtual_memory.htm');
