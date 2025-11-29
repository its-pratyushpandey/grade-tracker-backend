package com.codealpha.gradetracker.model;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "grades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {

    @Id
    private String id;

    @DBRef
    private Student student;

    @DBRef
    private Course course;

    @Indexed
    private String studentId;

    @Indexed
    private String courseId;

    @NotNull(message = "Numeric score is required")
    @Min(value = 0, message = "Score must be at least 0")
    @Max(value = 100, message = "Score cannot exceed 100")
    private Double numericScore;

    @Indexed
    private LocalDate gradeDate;

    private String description;

    private String assessment; // e.g., "Midterm", "Final", "Quiz 1", "Assignment 2"

    @Builder.Default
    private Double weight = 1.0; // Weight for weighted average calculations

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Computed field
    public String getLetterGrade() {
        if (numericScore >= 90) return "A";
        if (numericScore >= 80) return "B";
        if (numericScore >= 70) return "C";
        if (numericScore >= 60) return "D";
        return "F";
    }

    public String getGradeStatus() {
        return numericScore >= 60 ? "PASS" : "FAIL";
    }
}
