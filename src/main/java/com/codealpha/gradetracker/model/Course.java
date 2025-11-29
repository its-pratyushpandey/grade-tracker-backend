package com.codealpha.gradetracker.model;

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

@Document(collection = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    private String id;

    @NotBlank(message = "Course name is required")
    private String name;

    @NotBlank(message = "Course code is required")
    @Indexed(unique = true)
    private String code;

    private String description;

    @Builder.Default
    private Integer credits = 3;

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
}
