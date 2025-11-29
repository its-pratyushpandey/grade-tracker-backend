package com.codealpha.gradetracker.controller;

import com.codealpha.gradetracker.dto.GradeDTO;
import com.codealpha.gradetracker.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
@Tag(name = "Grades", description = "Grade management endpoints")
@SecurityRequirement(name = "bearer-jwt")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002", "http://localhost:5173"})
public class GradeController {

    private final GradeService gradeService;

    @GetMapping
    @Operation(summary = "Get all grades")
    public ResponseEntity<List<GradeDTO>> getAllGrades() {
        return ResponseEntity.ok(gradeService.getAllGrades());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get grade by ID")
    public ResponseEntity<GradeDTO> getGradeById(@PathVariable String id) {
        return ResponseEntity.ok(gradeService.getGradeById(id));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get grades by student ID")
    public ResponseEntity<List<GradeDTO>> getGradesByStudentId(@PathVariable String studentId) {
        return ResponseEntity.ok(gradeService.getGradesByStudentId(studentId));
    }

    @GetMapping("/student/{studentId}/paginated")
    @Operation(summary = "Get grades by student ID with pagination")
    public ResponseEntity<Page<GradeDTO>> getGradesByStudentIdPaginated(
            @PathVariable String studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(gradeService.getGradesByStudentId(studentId, pageable));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get grades by course ID")
    public ResponseEntity<List<GradeDTO>> getGradesByCourseId(@PathVariable String courseId) {
        return ResponseEntity.ok(gradeService.getGradesByCourseId(courseId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create grade", description = "Add a new grade (Admin only)")
    public ResponseEntity<GradeDTO> createGrade(@Valid @RequestBody GradeDTO gradeDTO) {
        return new ResponseEntity<>(gradeService.createGrade(gradeDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update grade", description = "Update an existing grade (Admin only)")
    public ResponseEntity<GradeDTO> updateGrade(
            @PathVariable String id,
            @Valid @RequestBody GradeDTO gradeDTO) {
        return ResponseEntity.ok(gradeService.updateGrade(id, gradeDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete grade", description = "Delete a grade (Admin only)")
    public ResponseEntity<Void> deleteGrade(@PathVariable String id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }
}
