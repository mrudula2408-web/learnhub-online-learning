package com.learnhub.service;

import com.learnhub.dto.QuizQuestionDTO;
import com.learnhub.dto.QuizSubmissionRequest;
import com.learnhub.entity.Course;
import com.learnhub.entity.QuizAttempt;
import com.learnhub.entity.Student;
import com.learnhub.repository.CourseRepository;
import com.learnhub.repository.QuizAttemptRepository;
import com.learnhub.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service class for quiz-related business logic.
 * Simple multiple-choice quiz questions are maintained here in the backend.
 * Scoring is computed securely on the backend, and the final attempt score
 * is persistently stored in MySQL in the 'quiz_attempts' table.
 */
@Service
public class QuizService {

    @Autowired
    private QuizAttemptRepository attemptRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    // Internal model for a quiz question with its correct answer (kept secret in backend)
    private static class QuestionInternal {
        final Long id;
        final String text;
        final String a;
        final String b;
        final String c;
        final String d;
        final String correctAnswer;

        QuestionInternal(Long id, String text, String a, String b, String c, String d, String correctAnswer) {
            this.id = id;
            this.text = text;
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.correctAnswer = correctAnswer;
        }

        QuizQuestionDTO toDTO() {
            return new QuizQuestionDTO(id, text, a, b, c, d);
        }
    }

    // Question bank per course ID
    private final Map<Long, List<QuestionInternal>> questionBank = new HashMap<>();
    private final List<QuestionInternal> defaultQuestions = new ArrayList<>();

    public QuizService() {
        initQuestionBank();
    }

    private void initQuestionBank() {
        // Course 1: Java Programming Quiz
        List<QuestionInternal> javaQuestions = List.of(
            new QuestionInternal(1L, "Which keyword is used to define a class in Java?",
                "define", "class", "Class", "struct", "B"),
            new QuestionInternal(2L, "What is the size of an int data type in Java?",
                "8 bits", "16 bits", "32 bits", "64 bits", "C"),
            new QuestionInternal(3L, "Which of the following is NOT an access modifier in Java?",
                "public", "private", "protected", "internal", "D"),
            new QuestionInternal(4L, "What does JVM stand for?",
                "Java Virtual Machine", "Java Variable Method", "Java Verified Module", "Joint Virtual Memory", "A"),
            new QuestionInternal(5L, "Which method is the starting entry point of a Java application?",
                "start()", "run()", "main()", "init()", "C")
        );
        questionBank.put(1L, javaQuestions);

        // Course 2: React JS Quiz
        List<QuestionInternal> reactQuestions = List.of(
            new QuestionInternal(1L, "What is JSX in React?",
                "A style language", "A syntax extension for JavaScript", "A database engine", "A build tool", "B"),
            new QuestionInternal(2L, "Which hook is used to manage state in a React functional component?",
                "useEffect", "useState", "useContext", "useReducer", "B"),
            new QuestionInternal(3L, "What is the Virtual DOM in React?",
                "Direct browser DOM", "In-memory DOM tree representation", "MySQL table", "Remote server copy", "B"),
            new QuestionInternal(4L, "How do parent components pass data down to child components?",
                "Via props", "Via SQL queries", "Via cookies", "Via local storage", "A"),
            new QuestionInternal(5L, "Which hook handles side effects such as data fetching?",
                "useState", "useEffect", "useMemo", "useRef", "B")
        );
        questionBank.put(2L, reactQuestions);

        // Course 3: Database Management with MySQL
        List<QuestionInternal> dbQuestions = List.of(
            new QuestionInternal(1L, "What does SQL stand for?",
                "Structured Query Language", "Simple Quick Logic", "System Quality Level", "Standard Query Link", "A"),
            new QuestionInternal(2L, "Which SQL command is used to retrieve data from a table?",
                "GET", "FETCH", "SELECT", "PULL", "C"),
            new QuestionInternal(3L, "Which key uniquely identifies each record in a database table?",
                "Foreign Key", "Primary Key", "Composite Key", "Unique Key", "B"),
            new QuestionInternal(4L, "Which clause is used to filter records in a SQL query?",
                "ORDER BY", "WHERE", "GROUP BY", "LIMIT", "B"),
            new QuestionInternal(5L, "Which constraint prevents duplicate values in a column?",
                "DEFAULT", "CHECK", "UNIQUE", "NOT NULL", "C")
        );
        questionBank.put(3L, dbQuestions);

        // Default Questions for other courses
        defaultQuestions.addAll(List.of(
            new QuestionInternal(1L, "Which data structure follows the First-In First-Out (FIFO) principle?",
                "Stack", "Queue", "Tree", "Graph", "B"),
            new QuestionInternal(2L, "What is the time complexity of binary search on a sorted array of size N?",
                "O(1)", "O(log N)", "O(N)", "O(N log N)", "B"),
            new QuestionInternal(3L, "Which protocol is standard for secure web communication?",
                "HTTP", "FTP", "HTTPS", "SMTP", "C"),
            new QuestionInternal(4L, "What is the primary role of an operating system kernel?",
                "Compile programs", "Manage system resources and hardware", "Render UI", "Format spreadsheets", "B"),
            new QuestionInternal(5L, "Which normal form removes partial functional dependencies?",
                "1NF", "2NF", "3NF", "BCNF", "B")
        ));
    }

    /**
     * Get quiz list for a given course.
     */
    public List<Map<String, Object>> getQuizzesForCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        String title = (course != null) ? course.getTitle() + " Assessment" : "Course Quiz";
        Map<String, Object> quizInfo = new HashMap<>();
        quizInfo.put("quizId", courseId);
        quizInfo.put("title", title);
        quizInfo.put("courseId", courseId);
        quizInfo.put("totalQuestions", 5);
        return List.of(quizInfo);
    }

    /**
     * Get questions for a course (answers hidden from client).
     */
    public List<QuizQuestionDTO> getQuestions(Long courseId) {
        List<QuestionInternal> questions = questionBank.getOrDefault(courseId, defaultQuestions);
        List<QuizQuestionDTO> dtoList = new ArrayList<>();
        for (QuestionInternal q : questions) {
            dtoList.add(q.toDTO());
        }
        return dtoList;
    }

    /**
     * Evaluate submitted answers, calculate score, and save result into MySQL quiz_attempts table.
     */
    public QuizAttempt submitQuiz(Long courseId, Long studentId, QuizSubmissionRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        List<QuestionInternal> questions = questionBank.getOrDefault(courseId, defaultQuestions);
        Map<Long, String> submittedAnswers = request.getAnswers();
        if (submittedAnswers == null) {
            submittedAnswers = Collections.emptyMap();
        }

        // Calculate score on the backend
        int score = 0;
        for (QuestionInternal q : questions) {
            String studentAnswer = submittedAnswers.get(q.id);
            if (studentAnswer != null && studentAnswer.trim().equalsIgnoreCase(q.correctAnswer)) {
                score++;
            }
        }

        String quizTitle = course.getTitle() + " Quiz";

        // Save attempt in MySQL database
        QuizAttempt attempt = new QuizAttempt(student, course, quizTitle, score, questions.size());
        return attemptRepository.save(attempt);
    }

    /**
     * Get all quiz attempts for a logged-in student.
     */
    public List<QuizAttempt> getStudentAttempts(Long studentId) {
        return attemptRepository.findByStudentIdOrderByAttemptDateDesc(studentId);
    }
}
