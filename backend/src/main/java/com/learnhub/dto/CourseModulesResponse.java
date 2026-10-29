package com.learnhub.dto;

import java.util.List;

public class CourseModulesResponse {
    private Long courseId;
    private String courseTitle;
    private String instructor;
    private int totalModules;
    private int completedCount;
    private int progressPercentage;
    private boolean quizUnlocked;
    private List<ModuleDTO> modules;

    public CourseModulesResponse() {}

    public CourseModulesResponse(Long courseId, String courseTitle, String instructor,
                                 int totalModules, int completedCount, int progressPercentage,
                                 boolean quizUnlocked, List<ModuleDTO> modules) {
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.instructor = instructor;
        this.totalModules = totalModules;
        this.completedCount = completedCount;
        this.progressPercentage = progressPercentage;
        this.quizUnlocked = quizUnlocked;
        this.modules = modules;
    }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public int getTotalModules() { return totalModules; }
    public void setTotalModules(int totalModules) { this.totalModules = totalModules; }

    public int getCompletedCount() { return completedCount; }
    public void setCompletedCount(int completedCount) { this.completedCount = completedCount; }

    public int getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(int progressPercentage) { this.progressPercentage = progressPercentage; }

    public boolean isQuizUnlocked() { return quizUnlocked; }
    public void setQuizUnlocked(boolean quizUnlocked) { this.quizUnlocked = quizUnlocked; }

    public List<ModuleDTO> getModules() { return modules; }
    public void setModules(List<ModuleDTO> modules) { this.modules = modules; }
}
