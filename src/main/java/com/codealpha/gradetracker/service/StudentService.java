package com.codealpha.gradetracker.service;

import com.codealpha.gradetracker.dto.StudentDTO;
import com.codealpha.gradetracker.exception.ResourceNotFoundException;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.repository.GradeRepository;
import com.codealpha.gradetracker.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentService {

    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional(readOnly = true)
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<StudentDTO> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public StudentDTO getStudentById(String id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return convertToDTO(student);
    }

    @Transactional(readOnly = true)
    public Page<StudentDTO> searchStudents(String search, Pageable pageable) {
        return studentRepository.searchStudents(search, pageable)
                .map(this::convertToDTO);
    }

    @Transactional
    public StudentDTO createStudent(StudentDTO studentDTO) {
        if (studentRepository.existsByEmail(studentDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + studentDTO.getEmail());
        }

        Student student = Student.builder()
                .firstName(studentDTO.getFirstName())
                .lastName(studentDTO.getLastName())
                .email(studentDTO.getEmail())
                .phoneNumber(studentDTO.getPhoneNumber())
                .address(studentDTO.getAddress())
                .enrollmentId(studentDTO.getEnrollmentId())
                .active(true)
                .build();

        Student saved = studentRepository.save(student);
        log.info("Created new student: {}", saved.getEmail());
        return convertToDTO(saved);
    }

    @Transactional
    public StudentDTO updateStudent(String id, StudentDTO studentDTO) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        // Check email uniqueness if changed
        if (!student.getEmail().equals(studentDTO.getEmail()) &&
                studentRepository.existsByEmail(studentDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + studentDTO.getEmail());
        }

        student.setFirstName(studentDTO.getFirstName());
        student.setLastName(studentDTO.getLastName());
        student.setEmail(studentDTO.getEmail());
        student.setPhoneNumber(studentDTO.getPhoneNumber());
        student.setAddress(studentDTO.getAddress());
        student.setEnrollmentId(studentDTO.getEnrollmentId());

        if (studentDTO.getActive() != null) {
            student.setActive(studentDTO.getActive());
        }

        Student updated = studentRepository.save(student);
        log.info("Updated student: {}", updated.getEmail());
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteStudent(String id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        studentRepository.delete(student);
        log.info("Deleted student: {}", student.getEmail());
    }

    @Transactional
    public void deactivateStudent(String id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        student.setActive(false);
        studentRepository.save(student);
        log.info("Deactivated student: {}", student.getEmail());
    }

    private StudentDTO convertToDTO(Student student) {
        var grades = gradeRepository.findByStudentId(student.getId());
        Double avgGrade = grades.isEmpty() ? null : 
            grades.stream()
                .mapToDouble(g -> g.getNumericScore())
                .average()
                .orElse(0.0);
        int totalGrades = grades.size();

        return StudentDTO.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail())
                .phoneNumber(student.getPhoneNumber())
                .address(student.getAddress())
                .enrollmentId(student.getEnrollmentId())
                .active(student.getActive())
                .createdAt(student.getCreatedAt() != null ? student.getCreatedAt().format(FORMATTER) : null)
                .updatedAt(student.getUpdatedAt() != null ? student.getUpdatedAt().format(FORMATTER) : null)
                .averageGrade(avgGrade)
                .totalGrades(totalGrades)
                .status(getStudentStatus(avgGrade))
                .build();
    }

    private String getStudentStatus(Double avgGrade) {
        if (avgGrade == null) return "NO_GRADES";
        if (avgGrade >= 90) return "EXCELLENT";
        if (avgGrade >= 80) return "GOOD";
        if (avgGrade >= 70) return "AVERAGE";
        if (avgGrade >= 60) return "SATISFACTORY";
        return "NEEDS_IMPROVEMENT";
    }
}
