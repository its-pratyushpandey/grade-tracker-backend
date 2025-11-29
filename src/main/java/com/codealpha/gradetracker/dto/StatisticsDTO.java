package com.codealpha.gradetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsDTO {

    // Overall statistics
    private Long totalStudents;
    private Long totalCourses;
    private Long totalGrades;
    private Long activeStudents;
    
    // Grade statistics
    private Double overallAverage;
    private Double median;
    private Double highestScore;
    private Double lowestScore;
    private Double standardDeviation;
    
    // Distribution
    private GradeDistribution distribution;
    private List<TopStudent> topStudents;
    private List<CoursePerformance> coursePerformances;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GradeDistribution {
        private Integer gradeA; // 90-100
        private Integer gradeB; // 80-89
        private Integer gradeC; // 70-79
        private Integer gradeD; // 60-69
        private Integer gradeF; // 0-59
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopStudent {
        private String studentId;
        private String studentName;
        private Double averageGrade;
        private Integer totalGrades;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CoursePerformance {
        private String courseId;
        private String courseName;
        private String courseCode;
        private Double averageGrade;
        private Integer totalStudents;
    }
}
