package com.codealpha.gradetracker.service;

import com.codealpha.gradetracker.dto.GradeDTO;
import com.codealpha.gradetracker.exception.ResourceNotFoundException;
import com.codealpha.gradetracker.model.Course;
import com.codealpha.gradetracker.model.Grade;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.repository.CourseRepository;
import com.codealpha.gradetracker.repository.GradeRepository;
import com.codealpha.gradetracker.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional(readOnly = true)
    public List<GradeDTO> getAllGrades() {
        return gradeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GradeDTO getGradeById(String id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade not found with id: " + id));
        return convertToDTO(grade);
    }

    @Transactional(readOnly = true)
    public List<GradeDTO> getGradesByStudentId(String studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }
        return gradeRepository.findByStudentId(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<GradeDTO> getGradesByStudentId(String studentId, Pageable pageable) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }
        return gradeRepository.findByStudentId(studentId, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public List<GradeDTO> getGradesByCourseId(String courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with id: " + courseId);
        }
        return gradeRepository.findByCourseId(courseId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public GradeDTO createGrade(GradeDTO gradeDTO) {
        Student student = studentRepository.findById(gradeDTO.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + gradeDTO.getStudentId()));

        Course course = courseRepository.findById(gradeDTO.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + gradeDTO.getCourseId()));

        Grade grade = Grade.builder()
                .student(student)
                .course(course)
                .studentId(student.getId())
                .courseId(course.getId())
                .numericScore(gradeDTO.getNumericScore())
                .gradeDate(LocalDate.parse(gradeDTO.getGradeDate(), DATE_FORMATTER))
                .description(gradeDTO.getDescription())
                .assessment(gradeDTO.getAssessment())
                .weight(gradeDTO.getWeight() != null ? gradeDTO.getWeight() : 1.0)
                .build();

        Grade saved = gradeRepository.save(grade);
        log.info("Created new grade for student: {} in course: {}", student.getEmail(), course.getCode());
        return convertToDTO(saved);
    }

    @Transactional
    public GradeDTO updateGrade(String id, GradeDTO gradeDTO) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade not found with id: " + id));

        // Update student and course if changed
        if (!grade.getStudentId().equals(gradeDTO.getStudentId())) {
            Student student = studentRepository.findById(gradeDTO.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + gradeDTO.getStudentId()));
            grade.setStudent(student);
            grade.setStudentId(student.getId());
        }

        if (!grade.getCourseId().equals(gradeDTO.getCourseId())) {
            Course course = courseRepository.findById(gradeDTO.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + gradeDTO.getCourseId()));
            grade.setCourse(course);
            grade.setCourseId(course.getId());
        }

        grade.setNumericScore(gradeDTO.getNumericScore());
        grade.setGradeDate(LocalDate.parse(gradeDTO.getGradeDate(), DATE_FORMATTER));
        grade.setDescription(gradeDTO.getDescription());
        grade.setAssessment(gradeDTO.getAssessment());
        grade.setWeight(gradeDTO.getWeight() != null ? gradeDTO.getWeight() : 1.0);

        Grade updated = gradeRepository.save(grade);
        log.info("Updated grade: {}", updated.getId());
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteGrade(String id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grade not found with id: " + id));
        gradeRepository.delete(grade);
        log.info("Deleted grade: {}", grade.getId());
    }

    private GradeDTO convertToDTO(Grade grade) {
        Student student = grade.getStudent();
        Course course = grade.getCourse();
        
        if (student == null && grade.getStudentId() != null) {
            student = studentRepository.findById(grade.getStudentId()).orElse(null);
        }
        if (course == null && grade.getCourseId() != null) {
            course = courseRepository.findById(grade.getCourseId()).orElse(null);
        }

        return GradeDTO.builder()
                .id(grade.getId())
                .studentId(grade.getStudentId())
                .courseId(grade.getCourseId())
                .numericScore(grade.getNumericScore())
                .gradeDate(grade.getGradeDate().format(DATE_FORMATTER))
                .description(grade.getDescription())
                .assessment(grade.getAssessment())
                .weight(grade.getWeight())
                .createdAt(grade.getCreatedAt() != null ? grade.getCreatedAt().format(DATETIME_FORMATTER) : null)
                .updatedAt(grade.getUpdatedAt() != null ? grade.getUpdatedAt().format(DATETIME_FORMATTER) : null)
                .studentName(student != null ? student.getFullName() : "Unknown")
                .courseName(course != null ? course.getName() : "Unknown")
                .courseCode(course != null ? course.getCode() : "Unknown")
                .letterGrade(grade.getLetterGrade())
                .gradeStatus(grade.getGradeStatus())
                .build();
    }
}
