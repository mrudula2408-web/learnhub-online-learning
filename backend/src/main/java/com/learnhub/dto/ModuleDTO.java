package com.learnhub.dto;

import java.time.LocalDateTime;

public class ModuleDTO {
    private Long id;
    private int moduleNumber;
    private String title;
    private String description;
    private String link1Title;
    private String link1Url;
    private String link2Title;
    private String link2Url;
    private boolean completed;
    private LocalDateTime completedAt;

    public ModuleDTO() {}

    public ModuleDTO(Long id, int moduleNumber, String title, String description,
                     String link1Title, String link1Url, String link2Title, String link2Url,
                     boolean completed, LocalDateTime completedAt) {
        this.id = id;
        this.moduleNumber = moduleNumber;
        this.title = title;
        this.description = description;
        this.link1Title = link1Title;
        this.link1Url = link1Url;
        this.link2Title = link2Title;
        this.link2Url = link2Url;
        this.completed = completed;
        this.completedAt = completedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
