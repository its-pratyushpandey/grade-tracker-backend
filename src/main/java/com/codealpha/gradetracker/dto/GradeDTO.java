package com.codealpha.gradetracker.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradeDTO {

    private String id;

    @NotNull(message = "Student ID is required")
    private String studentId;

    @NotNull(message = "Course ID is required")
    private String courseId;

    @NotNull(message = "Numeric score is required")
    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 100, message = "Score cannot exceed 100")
    private Double numericScore;

    @NotBlank(message = "Grade date is required")
    private String gradeDate;

    private String description;
    private String assessment;
    
    @Min(value = 0, message = "Weight must be positive")
    @Max(value = 100, message = "Weight cannot exceed 100")
    private Double weight;

    private String createdAt;
    private String updatedAt;
    
    // Additional info for responses
    private String studentName;
    private String courseName;
    private String courseCode;
    private String letterGrade;
    private String gradeStatus;
}
