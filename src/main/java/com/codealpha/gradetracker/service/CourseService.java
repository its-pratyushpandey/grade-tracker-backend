package com.codealpha.gradetracker.service;

import com.codealpha.gradetracker.dto.CourseDTO;
import com.codealpha.gradetracker.exception.ResourceNotFoundException;
import com.codealpha.gradetracker.model.Course;
import com.codealpha.gradetracker.repository.CourseRepository;
import com.codealpha.gradetracker.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final GradeRepository gradeRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional(readOnly = true)
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseDTO getCourseById(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return convertToDTO(course);
    }

    @Transactional(readOnly = true)
    public CourseDTO getCourseByCode(String code) {
        Course course = courseRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with code: " + code));
        return convertToDTO(course);
    }

    @Transactional
    public CourseDTO createCourse(CourseDTO courseDTO) {
        if (courseRepository.existsByCode(courseDTO.getCode())) {
            throw new IllegalArgumentException("Course code already exists: " + courseDTO.getCode());
        }

        Course course = Course.builder()
                .name(courseDTO.getName())
                .code(courseDTO.getCode())
                .description(courseDTO.getDescription())
                .credits(courseDTO.getCredits() != null ? courseDTO.getCredits() : 3)
                .active(true)
                .build();

        Course saved = courseRepository.save(course);
        log.info("Created new course: {}", saved.getCode());
        return convertToDTO(saved);
    }

    @Transactional
    public CourseDTO updateCourse(String id, CourseDTO courseDTO) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        // Check code uniqueness if changed
        if (!course.getCode().equals(courseDTO.getCode()) &&
                courseRepository.existsByCode(courseDTO.getCode())) {
            throw new IllegalArgumentException("Course code already exists: " + courseDTO.getCode());
        }

        course.setName(courseDTO.getName());
        course.setCode(courseDTO.getCode());
        course.setDescription(courseDTO.getDescription());
        course.setCredits(courseDTO.getCredits());

        if (courseDTO.getActive() != null) {
            course.setActive(courseDTO.getActive());
        }

        Course updated = courseRepository.save(course);
        log.info("Updated course: {}", updated.getCode());
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteCourse(String id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        courseRepository.delete(course);
        log.info("Deleted course: {}", course.getCode());
    }

    private CourseDTO convertToDTO(Course course) {
        var grades = gradeRepository.findByCourseId(course.getId());
        Double avgGrade = grades.isEmpty() ? null : 
            grades.stream()
                .mapToDouble(g -> g.getNumericScore())
                .average()
                .orElse(0.0);
        long enrolledStudents = grades.stream()
                .map(grade -> grade.getStudentId())
                .distinct()
                .count();

        return CourseDTO.builder()
                .id(course.getId())
                .name(course.getName())
                .code(course.getCode())
                .description(course.getDescription())
                .credits(course.getCredits())
                .active(course.getActive())
                .createdAt(course.getCreatedAt() != null ? course.getCreatedAt().format(FORMATTER) : null)
                .updatedAt(course.getUpdatedAt() != null ? course.getUpdatedAt().format(FORMATTER) : null)
                .averageGrade(avgGrade)
                .enrolledStudents((int) enrolledStudents)
                .build();
    }
}
