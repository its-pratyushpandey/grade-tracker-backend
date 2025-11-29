package com.codealpha.gradetracker.controller;

import com.codealpha.gradetracker.dto.CourseDTO;
import com.codealpha.gradetracker.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Courses", description = "Course management endpoints")
@SecurityRequirement(name = "bearer-jwt")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "http://localhost:5173"})
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    @Operation(summary = "Get all courses")
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable String id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get course by code")
    public ResponseEntity<CourseDTO> getCourseByCode(@PathVariable String code) {
        return ResponseEntity.ok(courseService.getCourseByCode(code));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create course", description = "Create a new course (Admin only)")
    public ResponseEntity<CourseDTO> createCourse(@Valid @RequestBody CourseDTO courseDTO) {
        return new ResponseEntity<>(courseService.createCourse(courseDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update course", description = "Update an existing course (Admin only)")
    public ResponseEntity<CourseDTO> updateCourse(
            @PathVariable String id,
            @Valid @RequestBody CourseDTO courseDTO) {
        return ResponseEntity.ok(courseService.updateCourse(id, courseDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete course", description = "Delete a course (Admin only)")
    public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
