package com.codealpha.gradetracker.controller;

import com.codealpha.gradetracker.dto.StudentDTO;
import com.codealpha.gradetracker.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Student management endpoints")
@SecurityRequirement(name = "bearer-jwt")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    @Operation(summary = "Get all students", description = "Retrieve all students with pagination")
    public ResponseEntity<Page<StudentDTO>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(studentService.getAllStudents(pageable));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all students without pagination")
    public ResponseEntity<List<StudentDTO>> getAllStudentsWithoutPagination() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable String id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search students", description = "Search students by name, email, or enrollment ID")
    public ResponseEntity<Page<StudentDTO>> searchStudents(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(studentService.searchStudents(query, pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create student", description = "Create a new student (Admin only)")
    public ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody StudentDTO studentDTO) {
        return new ResponseEntity<>(studentService.createStudent(studentDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update student", description = "Update an existing student (Admin only)")
    public ResponseEntity<StudentDTO> updateStudent(
            @PathVariable String id,
            @Valid @RequestBody StudentDTO studentDTO) {
        return ResponseEntity.ok(studentService.updateStudent(id, studentDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete student", description = "Delete a student (Admin only)")
    public ResponseEntity<Void> deleteStudent(@PathVariable String id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate student", description = "Deactivate a student (Admin only)")
    public ResponseEntity<Void> deactivateStudent(@PathVariable String id) {
        studentService.deactivateStudent(id);
        return ResponseEntity.noContent().build();
    }
}
