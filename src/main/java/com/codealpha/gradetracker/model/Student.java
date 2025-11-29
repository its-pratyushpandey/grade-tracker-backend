package com.codealpha.gradetracker.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    private String id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Indexed(unique = true)
    private String email;

    private String phoneNumber;

    private String address;

    @Indexed
    private String enrollmentId;

    @DBRef
    @Builder.Default
    private List<Grade> grades = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder.Default
    private Boolean active = true;

    // Helper methods
    public void addGrade(Grade grade) {
        grades.add(grade);
    }

    public void removeGrade(Grade grade) {
        grades.remove(grade);
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
