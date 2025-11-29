package com.codealpha.gradetracker.controller;

import com.codealpha.gradetracker.dto.StatisticsDTO;
import com.codealpha.gradetracker.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "Statistics endpoints")
@SecurityRequirement(name = "bearer-jwt")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping
    @Operation(summary = "Get overall statistics", description = "Get comprehensive statistics for all students and grades")
    public ResponseEntity<StatisticsDTO> getOverallStatistics() {
        return ResponseEntity.ok(statisticsService.getOverallStatistics());
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get student statistics", description = "Get statistics for a specific student")
    public ResponseEntity<Map<String, Object>> getStudentStatistics(@PathVariable String studentId) {
        return ResponseEntity.ok(statisticsService.getStudentStatistics(studentId));
    }
}
