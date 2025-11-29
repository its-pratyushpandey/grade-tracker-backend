package com.codealpha.gradetracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDTO {

    private String id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    private String phoneNumber;
    private String address;
    private String enrollmentId;
    private Boolean active;
    private String createdAt;
    private String updatedAt;
    
    // Computed fields
    private Double averageGrade;
    private Integer totalGrades;
    private String status; // EXCELLENT, GOOD, AVERAGE, POOR
}
