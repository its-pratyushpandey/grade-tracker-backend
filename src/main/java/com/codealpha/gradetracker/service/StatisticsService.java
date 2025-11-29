package com.codealpha.gradetracker.service;

import com.codealpha.gradetracker.dto.StatisticsDTO;
import com.codealpha.gradetracker.model.Grade;
import com.codealpha.gradetracker.repository.CourseRepository;
import com.codealpha.gradetracker.repository.GradeRepository;
import com.codealpha.gradetracker.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final GradeRepository gradeRepository;

    @Transactional(readOnly = true)
    public StatisticsDTO getOverallStatistics() {
        long totalStudents = studentRepository.count();
        long totalCourses = courseRepository.count();
        long totalGrades = gradeRepository.countAllGrades();
        long activeStudents = studentRepository.countActiveStudents();

        // Fetch all grades once and cache them
        List<Grade> allGrades = gradeRepository.findAll();
        
        // Work with scores only to avoid @DBRef lazy loading issues
        List<Double> allScores = allGrades.stream()
                .map(Grade::getNumericScore)
                .sorted()
                .collect(Collectors.toList());
        
        Double overallAverage = allScores.isEmpty() ? null : 
            allScores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        Double median = calculateMedian(allScores);
        Double highestScore = allScores.isEmpty() ? null : allScores.get(allScores.size() - 1);
        Double lowestScore = allScores.isEmpty() ? null : allScores.get(0);
        Double standardDeviation = calculateStandardDeviation(allScores, overallAverage);

        StatisticsDTO.GradeDistribution distribution = calculateGradeDistribution(allGrades);
        List<StatisticsDTO.TopStudent> topStudents = getTopStudents(allGrades, 5);
        List<StatisticsDTO.CoursePerformance> coursePerformances = getCoursePerformances(allGrades);

        return StatisticsDTO.builder()
                .totalStudents(totalStudents)
                .totalCourses(totalCourses)
                .totalGrades(totalGrades)
                .activeStudents(activeStudents)
                .overallAverage(overallAverage)
                .median(median)
                .highestScore(highestScore)
                .lowestScore(lowestScore)
                .standardDeviation(standardDeviation)
                .distribution(distribution)
                .topStudents(topStudents)
                .coursePerformances(coursePerformances)
                .build();
    }

    private Double calculateMedian(List<Double> scores) {
        if (scores.isEmpty()) return null;
        
        int size = scores.size();
        if (size % 2 == 0) {
            return (scores.get(size / 2 - 1) + scores.get(size / 2)) / 2.0;
        } else {
            return scores.get(size / 2);
        }
    }

    private Double calculateStandardDeviation(List<Double> scores, Double mean) {
        if (scores.isEmpty() || mean == null) return null;

        double variance = scores.stream()
                .mapToDouble(score -> Math.pow(score - mean, 2))
                .average()
                .orElse(0.0);

        return Math.sqrt(variance);
    }

    private StatisticsDTO.GradeDistribution calculateGradeDistribution(List<Grade> allGrades) {
        int gradeA = 0, gradeB = 0, gradeC = 0, gradeD = 0, gradeF = 0;
        
        for (Grade grade : allGrades) {
            double score = grade.getNumericScore();
            if (score >= 90) gradeA++;
            else if (score >= 80) gradeB++;
            else if (score >= 70) gradeC++;
            else if (score >= 60) gradeD++;
            else gradeF++;
        }

        return StatisticsDTO.GradeDistribution.builder()
                .gradeA(gradeA)
                .gradeB(gradeB)
                .gradeC(gradeC)
                .gradeD(gradeD)
                .gradeF(gradeF)
                .build();
    }

    private List<StatisticsDTO.TopStudent> getTopStudents(List<Grade> allGrades, int limit) {
        Map<String, List<Grade>> gradesByStudent = allGrades.stream()
                .filter(g -> g.getStudentId() != null)
                .collect(Collectors.groupingBy(Grade::getStudentId));

        return gradesByStudent.entrySet().stream()
                .map(entry -> {
                    String studentId = entry.getKey();
                    List<Grade> grades = entry.getValue();
                    Double average = grades.stream()
                            .mapToDouble(Grade::getNumericScore)
                            .average()
                            .orElse(0.0);
                    
                    var student = studentRepository.findById(studentId).orElse(null);
                    
                    return StatisticsDTO.TopStudent.builder()
                            .studentId(studentId)
                            .studentName(student != null ? student.getFullName() : "Unknown")
                            .averageGrade(average)
                            .totalGrades(grades.size())
                            .build();
                })
                .sorted(Comparator.comparing(StatisticsDTO.TopStudent::getAverageGrade).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<StatisticsDTO.CoursePerformance> getCoursePerformances(List<Grade> allGrades) {
        Map<String, List<Grade>> gradesByCourse = allGrades.stream()
                .filter(g -> g.getCourseId() != null)
                .collect(Collectors.groupingBy(Grade::getCourseId));

        return gradesByCourse.entrySet().stream()
                .map(entry -> {
                    String courseId = entry.getKey();
                    List<Grade> grades = entry.getValue();
                    Double average = grades.stream()
                            .mapToDouble(Grade::getNumericScore)
                            .average()
                            .orElse(0.0);
                    
                    long uniqueStudents = grades.stream()
                            .map(Grade::getStudentId)
                            .distinct()
                            .count();

                    var course = courseRepository.findById(courseId).orElse(null);

                    return StatisticsDTO.CoursePerformance.builder()
                            .courseId(courseId)
                            .courseName(course != null ? course.getName() : "Unknown")
                            .courseCode(course != null ? course.getCode() : "Unknown")
                            .averageGrade(average)
                            .totalStudents((int) uniqueStudents)
                            .build();
                })
                .sorted(Comparator.comparing(StatisticsDTO.CoursePerformance::getAverageGrade).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getStudentStatistics(String studentId) {
        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        Double average = grades.isEmpty() ? null : 
            grades.stream().mapToDouble(Grade::getNumericScore).average().orElse(0.0);
        Double highest = grades.stream()
                .mapToDouble(Grade::getNumericScore)
                .max()
                .orElse(0.0);
        Double lowest = grades.stream()
                .mapToDouble(Grade::getNumericScore)
                .min()
                .orElse(0.0);

        Map<String, Object> stats = new HashMap<>();
        stats.put("average", average);
        stats.put("highest", highest);
        stats.put("lowest", lowest);
        stats.put("totalGrades", grades.size());
        stats.put("passing", grades.stream().filter(g -> g.getNumericScore() >= 60).count());
        stats.put("failing", grades.stream().filter(g -> g.getNumericScore() < 60).count());

        return stats;
    }
}
