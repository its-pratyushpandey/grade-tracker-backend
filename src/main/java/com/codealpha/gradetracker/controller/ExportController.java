package com.codealpha.gradetracker.controller;

import com.codealpha.gradetracker.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
@Tag(name = "Export", description = "Export endpoints for grades and reports")
@SecurityRequirement(name = "bearer-jwt")
public class ExportController {

    private final ExportService exportService;

    @GetMapping("/students/csv")
    @Operation(summary = "Export students to CSV")
    public ResponseEntity<String> exportStudentsToCSV() throws IOException {
        String csv = exportService.exportStudentsToCSV();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=students.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csv);
    }

    @GetMapping("/grades/csv")
    @Operation(summary = "Export all grades to CSV")
    public ResponseEntity<String> exportGradesToCSV() throws IOException {
        String csv = exportService.exportGradesToCSV();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=grades.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csv);
    }

    @GetMapping("/student/{studentId}/csv")
    @Operation(summary = "Export student grades to CSV")
    public ResponseEntity<String> exportStudentGradesToCSV(@PathVariable String studentId) throws IOException {
        String csv = exportService.exportStudentGradesToCSV(studentId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_" + studentId + "_grades.csv");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(csv);
    }

    @GetMapping("/grades/pdf")
    @Operation(summary = "Export all grades to PDF")
    public ResponseEntity<byte[]> exportGradesToPDF() throws IOException {
        byte[] pdf = exportService.exportGradesToPDF();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=grades.pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }

    @GetMapping("/student/{studentId}/pdf")
    @Operation(summary = "Export student grades to PDF")
    public ResponseEntity<byte[]> exportStudentGradesToPDF(@PathVariable String studentId) throws IOException {
        byte[] pdf = exportService.exportStudentGradesToPDF(studentId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_" + studentId + "_report.pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }
}
