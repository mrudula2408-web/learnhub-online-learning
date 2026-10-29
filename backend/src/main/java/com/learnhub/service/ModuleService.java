package com.learnhub.service;

import com.learnhub.dto.CourseModulesResponse;
import com.learnhub.dto.ModuleDTO;
import com.learnhub.entity.*;
import com.learnhub.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for learning modules and completion tracking.
 * Each course has 3 modules that must be completed before the quiz unlocks.
 */
@Service
public class ModuleService {

    @Autowired
    private CourseModuleRepository moduleRepository;

    @Autowired
    private ModuleCompletionRepository completionRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private LearningProgressRepository progressRepository;

    /**
     * Get learning modules for a course with the student's completion status.
     */
    public CourseModulesResponse getCourseModules(Long studentId, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        List<CourseModule> moduleList = moduleRepository.findByCourseIdOrderByModuleNumberAsc(courseId);

        // Fetch completions for this student
        List<ModuleCompletion> completions = (studentId != null)
                ? completionRepository.findByStudentIdAndCourseId(studentId, courseId)
                : Collections.emptyList();

        Map<Long, LocalDateTime> completionMap = completions.stream()
                .collect(Collectors.toMap(c -> c.getModule().getId(), ModuleCompletion::getCompletedAt, (a, b) -> a));

        List<ModuleDTO> dtos = new ArrayList<>();
        for (CourseModule m : moduleList) {
            boolean isCompleted = completionMap.containsKey(m.getId());
            LocalDateTime completedAt = completionMap.get(m.getId());
            dtos.add(new ModuleDTO(
                    m.getId(),
                    m.getModuleNumber(),
                    m.getTitle(),
                    m.getDescription(),
                    m.getLink1Title(),
                    m.getLink1Url(),
                    m.getLink2Title(),
                    m.getLink2Url(),
                    isCompleted,
                    completedAt
            ));
        }

        int total = moduleList.size();
        int completed = (int) dtos.stream().filter(ModuleDTO::isCompleted).count();
        int percentage = total > 0 ? (int) Math.round(((double) completed / total) * 100) : 0;
        boolean quizUnlocked = total > 0 && completed >= total;

        return new CourseModulesResponse(
                course.getId(),
                course.getTitle(),
                course.getInstructor(),
                total,
                completed,
                percentage,
                quizUnlocked,
                dtos
        );
    }

    /**
     * Mark a module as completed by the student.
     * Prevents duplicate completions and automatically updates LearningProgress in MySQL.
     */
    @Transactional
    public Map<String, Object> completeModule(Long studentId, Long moduleId) {
        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = module.getCourse();

        // Check if student is enrolled; if not, throw clear exception
        if (!enrollmentRepository.existsByStudentIdAndCourseId(studentId, course.getId())) {
            throw new RuntimeException("Please enroll in this course before completing modules.");
        }

        // Save completion if not already marked
        if (!completionRepository.existsByStudentIdAndModuleId(studentId, moduleId)) {
            ModuleCompletion completion = new ModuleCompletion(student, module, course);
            completionRepository.save(completion);
        }

        // Calculate progress percentage: (completed / total) * 100
        long totalModules = moduleRepository.countByCourseId(course.getId());
        long completedModules = completionRepository.countByStudentIdAndCourseId(studentId, course.getId());
        int percentage = totalModules > 0 ? (int) Math.round(((double) completedModules / totalModules) * 100) : 0;
        String status = percentage >= 100 ? "COMPLETED" : "IN_PROGRESS";

        // Update or create LearningProgress in MySQL
        LearningProgress progress = progressRepository.findByStudentIdAndCourseId(studentId, course.getId())
                .orElseGet(() -> new LearningProgress(student, course));
        progress.setCompletionPercentage(percentage);
        progress.setCompletionStatus(status);
        progressRepository.save(progress);

        boolean quizUnlocked = totalModules > 0 && completedModules >= totalModules;

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Module marked as completed!");
        result.put("moduleId", moduleId);
        result.put("completedCount", completedModules);
        result.put("totalModules", totalModules);
        result.put("progressPercentage", percentage);
        result.put("quizUnlocked", quizUnlocked);
        return result;
    }

    /**
     * Check if a student has completed all modules for a course.
     */
    public boolean areAllModulesCompleted(Long studentId, Long courseId) {
        long total = moduleRepository.countByCourseId(courseId);
        if (total == 0) return true; // If course has no modules, don't lock
        long completed = completionRepository.countByStudentIdAndCourseId(studentId, courseId);
        return completed >= total;
    }

    /**
     * Get module completion counts for a student across all enrolled courses.
     */
    public Map<Long, Map<String, Object>> getStudentProgressSummary(Long studentId) {
        List<ModuleCompletion> allCompletions = completionRepository.findByStudentId(studentId);
        Map<Long, Long> countsByCourse = allCompletions.stream()
                .collect(Collectors.groupingBy(c -> c.getCourse().getId(), Collectors.counting()));

        Map<Long, Map<String, Object>> summary = new HashMap<>();
        for (Course course : courseRepository.findAll()) {
            long total = moduleRepository.countByCourseId(course.getId());
            long completed = countsByCourse.getOrDefault(course.getId(), 0L);
            int pct = total > 0 ? (int) Math.round(((double) completed / total) * 100) : 0;
            boolean quizUnlocked = total > 0 && completed >= total;

            Map<String, Object> item = new HashMap<>();
            item.put("totalModules", total);
            item.put("completedModules", completed);
            item.put("percentage", pct);
            item.put("quizUnlocked", quizUnlocked);
            summary.put(course.getId(), item);
        }
        return summary;
    }
}
