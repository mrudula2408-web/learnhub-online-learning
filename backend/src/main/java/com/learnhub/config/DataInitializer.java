package com.learnhub.config;

import com.learnhub.entity.Course;
import com.learnhub.entity.CourseModule;
import com.learnhub.repository.CourseModuleRepository;
import com.learnhub.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Initializes sample courses and course-specific learning modules on startup.
 * Automatically populates MySQL if tables are empty, preventing duplicates.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseModuleRepository moduleRepository;

    @Override
    public void run(String... args) {
        // Step 1: Ensure 5 core courses exist
        if (courseRepository.count() == 0) {
            courseRepository.save(new Course(
                "Introduction to Java Programming",
                "Learn fundamental Java concepts including variables, loops, object-oriented programming (OOP), classes, inheritance, and methods. Perfect for beginners.",
                "Dr. Ravi Kumar",
                "10 hours"
            ));

            courseRepository.save(new Course(
                "Web Development with React",
                "Build modern web user interfaces using React JS components, state hooks, props, and connecting to Spring Boot REST APIs.",
                "Prof. Anita Sharma",
                "12 hours"
            ));

            courseRepository.save(new Course(
                "Database Management with MySQL",
                "Learn relational databases, SQL queries (SELECT, INSERT, UPDATE, DELETE), primary/foreign keys, joins, and JDBC connectivity.",
                "Dr. Suresh Patel",
                "8 hours"
            ));

            courseRepository.save(new Course(
                "Data Structures and Algorithms",
                "Master core DSA concepts in Java: Arrays, Linked Lists, Stacks, Queues, Trees, Searching, and Sorting algorithms with time complexity.",
                "Prof. Deepa Nair",
                "14 hours"
            ));

            courseRepository.save(new Course(
                "Operating Systems Fundamentals",
                "Core principles of modern operating systems: Processes, CPU scheduling, Memory management, Virtual memory, Deadlocks, and File systems.",
                "Dr. Arjun Mehta",
                "9 hours"
            ));

            System.out.println("LearnHub: 5 initial sample courses added to MySQL courses table.");
        }

        // Step 2: Ensure 3 learning modules exist for each course
        if (moduleRepository.count() == 0) {
            List<Course> courses = courseRepository.findAll();
            for (Course course : courses) {
                String title = course.getTitle();
                if (title.contains("Java")) {
                    moduleRepository.save(new CourseModule(
                        course, 1, "Module 1: Java Basics",
                        "Introduction to Java syntax, JVM, JRE, JDK, bytecode execution, and writing your first Java program.",
                        "Official Java Learning Portal (dev.java)", "https://dev.java/learn/",
                        "Oracle Java Tutorials", "https://docs.oracle.com/javase/tutorial/"
                    ));
                    moduleRepository.save(new CourseModule(
                        course, 2, "Module 2: Variables and Control Statements",
                        "Primitive data types, variable scopes, arithmetic/logical operators, if-else decision making, and loop iterations.",
                        "W3Schools Java Variables & Conditions", "https://www.w3schools.com/java/java_variables.asp",
                        "GeeksforGeeks Control Statements", "https://www.geeksforgeeks.org/decision-making-javaif-else-switch-break-continue-jump/"
                    ));
                    moduleRepository.save(new CourseModule(
                        course, 3, "Module 3: Object-Oriented Programming",
                        "Classes, objects, constructors, inheritance, polymorphism, abstraction, and encapsulation in Java.",
                        "Oracle OOP Concepts Guide", "https://docs.oracle.com/javase/tutorial/java/concepts/",
                        "JavaTpoint Java OOPs Concepts", "https://www.javatpoint.com/java-oops-concepts"
                    ));
                } else if (title.contains("React")) {
                    moduleRepository.save(new CourseModule(
                        course, 1, "Module 1: React Fundamentals & JSX",
                        "Understanding the React component architecture, JSX syntax, Virtual DOM, and rendering elements.",
                        "React.dev Official Quick Start", "https://react.dev/learn",
                        "MDN Getting Started with React", "https://developer.mozilla.org/en-US/docs/Learn/Tools_and_testing/Client-side_JavaScript_frameworks/React_getting_started"
                    ));
                    moduleRepository.save(new CourseModule(
                        course, 2, "Module 2: Components, Props, and State",
                        "Functional components, passing data using immutable props, and managing reactive local state with useState.",
                        "React.dev State as a Component's Memory", "https://react.dev/learn/state-a-components-memory",
                        "W3Schools React Props & State", "https://www.w3schools.com/react/react_props.asp"
                    ));
                    moduleRepository.save(new CourseModule(
                        course, 3, "Module 3: Side Effects & REST API Integration",
                        "Managing component lifecycle with useEffect, asynchronous fetch calls to Spring Boot REST APIs, and error handling.",
                        "React.dev Synchronizing with Effects", "https://react.dev/learn/synchronizing-with-effects",
                        "FreeCodeCamp Fetch API in React", "https://www.freecodecamp.org/news/how-to-fetch-api-data-in-react/"
                    ));
                } else if (title.contains("MySQL") || title.contains("Database")) {
                    moduleRepository.save(new CourseModule(
                        course, 1, "Module 1: Relational Database Concepts & SQL Basics",
                        "Fundamentals of relational databases, schema design, primary and foreign keys, and MySQL Workbench essentials.",
                        "MySQL Official Reference Manual", "https://dev.mysql.com/doc/refman/8.0/en/",
                        "W3Schools SQL Introduction", "https://www.w3schools.com/sql/sql_intro.asp"
                    ));
                    moduleRepository.save(new CourseModule(
                        course, 2, "Module 2: CRUD Operations & Querying",
                        "Writing efficient SQL queries using SELECT, INSERT, UPDATE, DELETE, WHERE filtering, and aggregation functions.",
                        "SQL Tutorial - SQL Basics", "https://www.sqltutorial.org/sql-basic/",
                        "MySQL Query Syntax Reference", "https://dev.mysql.com/doc/refman/8.0/en/sql-syntax.html"
                    ));
                    moduleRepository.save(new CourseModule(
                        course, 3, "Module 3: Joins, Constraints & Indexing",
                        "Combining tables with INNER JOIN, LEFT JOIN, database normalization (1NF, 2NF, 3NF), and foreign key constraints.",
                        "W3Schools SQL Joins Tutorial", "https://www.w3schools.com/sql/sql_join.asp",
                        "GeeksforGeeks Database Normalization", "https://www.geeksforgeeks.org/introduction-of-database-normalization/"
                    ));
                } else if (title.contains("Data Structures") || title.contains("Algorithms")) {
                    moduleRepository.save(new CourseModule(
                        course, 1, "Module 1: Complexity Analysis & Linear Data Structures",
                        "Big-O asymptotic notation, time/space complexity, static arrays, dynamic lists, and Singly/Doubly Linked Lists.",
                        "GeeksforGeeks Data Structures Guide", "https://www.geeksforgeeks.org/data-structures/",
                        "Programiz DSA Linear Structures", "https://www.programiz.com/dsa"
                    ));
                    moduleRepository.save(new CourseModule(
                        course, 2, "Module 2: Stacks, Queues & Hashing",
                        "Stack LIFO operations, Queue FIFO implementations, circular queues, hashing techniques, and Java HashMap.",
                        "GeeksforGeeks Stack & Queue Guide", "https://www.geeksforgeeks.org/stack-data-structure/",
                        "Baeldung Guide to Java HashMap", "https://www.baeldung.com/java-hashmap"
                    ));
                    moduleRepository.save(new CourseModule(
                        course, 3, "Module 3: Trees, Graphs & Sorting Algorithms",
                        "Binary search trees, tree traversals (inorder, preorder, postorder), binary search, merge sort, and quick sort.",
                        "GeeksforGeeks Binary Search Tree", "https://www.geeksforgeeks.org/binary-search-tree-data-structure/",
                        "Programiz Sorting Algorithms Tutorial", "https://www.programiz.com/dsa/sorting-algorithm"
                    ));
                } else if (title.contains("Operating Systems")) {
                    moduleRepository.save(new CourseModule(
                        course, 1, "Module 1: Processes, Threads & CPU Scheduling",
                        "Process states, process control blocks (PCB), context switching, multithreading, and CPU scheduling algorithms.",
                        "GeeksforGeeks Operating Systems Tutorial", "https://www.geeksforgeeks.org/operating-systems/",
                        "TutorialsPoint OS Process Management", "https://www.tutorialspoint.com/operating_system/os_processes.htm"
                    ));
                    moduleRepository.save(new CourseModule(
                        course, 2, "Module 2: Process Synchronization & Deadlocks",
                        "Race conditions, critical sections, semaphores, deadlock conditions, deadlock prevention, and Banker's Algorithm.",
                        "GeeksforGeeks Process Synchronization", "https://www.geeksforgeeks.org/process-synchronization-in-operating-system/",
                        "JavaTpoint OS Deadlocks Introduction", "https://www.javatpoint.com/os-deadlocks-introduction"
                    ));
                    moduleRepository.save(new CourseModule(
                        course, 3, "Module 3: Memory Management & File Systems",
                        "Contiguous memory allocation, paging, segmentation, virtual memory concepts, and page replacement algorithms.",
                        "GeeksforGeeks Memory Management in OS", "https://www.geeksforgeeks.org/memory-management-in-operating-system/",
                        "TutorialsPoint Virtual Memory in OS", "https://www.tutorialspoint.com/operating_system/os_virtual_memory.htm"
                    ));
                }
            }
            System.out.println("LearnHub: 15 course-specific learning modules added to MySQL course_modules table.");
        }
    }
}
