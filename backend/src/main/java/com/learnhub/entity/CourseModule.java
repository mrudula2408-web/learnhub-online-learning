package com.learnhub.entity;

import jakarta.persistence.*;

/**
 * Entity representing a learning module belonging to a course.
 * Each course has 3 modules that a student must complete before taking the quiz.
 */
@Entity
@Table(
    name = "course_modules",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_course_module",
        columnNames = {"course_id", "module_number"}
    )
)
public class CourseModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "module_number", nullable = false)
    private int moduleNumber; // 1, 2, or 3

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "link1_title", length = 200)
    private String link1Title;

    @Column(name = "link1_url", length = 500)
    private String link1Url;

    @Column(name = "link2_title", length = 200)
    private String link2Title;

    @Column(name = "link2_url", length = 500)
    private String link2Url;

    public CourseModule() {
    }

    public CourseModule(Course course, int moduleNumber, String title, String description,
                        String link1Title, String link1Url, String link2Title, String link2Url) {
        this.course = course;
        this.moduleNumber = moduleNumber;
        this.title = title;
        this.description = description;
        this.link1Title = link1Title;
        this.link1Url = link1Url;
        this.link2Title = link2Title;
        this.link2Url = link2Url;
    }

    // ---- Getters and Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public int getModuleNumber() { return moduleNumber; }
    public void setModuleNumber(int moduleNumber) { this.moduleNumber = moduleNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLink1Title() { return link1Title; }
    public void setLink1Title(String link1Title) { this.link1Title = link1Title; }

    public String getLink1Url() { return link1Url; }
    public void setLink1Url(String link1Url) { this.link1Url = link1Url; }

    public String getLink2Title() { return link2Title; }
    public void setLink2Title(String link2Title) { this.link2Title = link2Title; }

    public String getLink2Url() { return link2Url; }
    public void setLink2Url(String link2Url) { this.link2Url = link2Url; }
}
