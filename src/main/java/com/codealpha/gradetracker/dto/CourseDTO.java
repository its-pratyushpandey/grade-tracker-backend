package com.codealpha.gradetracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDTO {

    private String id;

    @NotBlank(message = "Course name is required")
    private String name;

    @NotBlank(message = "Course code is required")
    private String code;

    private String description;
    private Integer credits;
    private Boolean active;
    private String createdAt;
    private String updatedAt;
    
    // Computed fields
    private Double averageGrade;
    private Integer enrolledStudents;
}
