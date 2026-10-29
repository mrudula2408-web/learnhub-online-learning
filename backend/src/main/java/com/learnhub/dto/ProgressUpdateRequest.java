package com.learnhub.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for updating a student's learning progress.
 */
public class ProgressUpdateRequest {

    @Min(value = 0, message = "Completion percentage cannot be less than 0")
    @Max(value = 100, message = "Completion percentage cannot exceed 100")
    private int completionPercentage;

    @NotBlank(message = "Completion status is required")
    private String completionStatus; // "IN_PROGRESS" or "COMPLETED"

    // ---- Getters and Setters ----

    public int getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(int completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public String getCompletionStatus() { return completionStatus; }
    public void setCompletionStatus(String completionStatus) {
        this.completionStatus = completionStatus;
    }
}
