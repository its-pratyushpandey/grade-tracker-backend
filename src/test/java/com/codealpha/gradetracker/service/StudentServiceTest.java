package com.codealpha.gradetracker.service;

import com.codealpha.gradetracker.dto.StudentDTO;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.repository.GradeRepository;
import com.codealpha.gradetracker.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private GradeRepository gradeRepository;

    @InjectMocks
    private StudentService studentService;

    private Student testStudent;
    private StudentDTO testStudentDTO;

    @BeforeEach
    void setUp() {
        testStudent = Student.builder()
                .id("507f1f77bcf86cd799439011")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .phoneNumber("555-0100")
                .enrollmentId("TEST001")
                .active(true)
                .build();

        testStudentDTO = StudentDTO.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .phoneNumber("555-0100")
                .enrollmentId("TEST001")
                .build();
    }

    @Test
    void createStudent_Success() {
        when(studentRepository.existsByEmail(testStudentDTO.getEmail())).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);
        when(gradeRepository.findByStudentId(any())).thenReturn(java.util.Collections.emptyList());

        StudentDTO result = studentService.createStudent(testStudentDTO);

        assertNotNull(result);
        assertEquals(testStudentDTO.getEmail(), result.getEmail());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void createStudent_EmailAlreadyExists_ThrowsException() {
        when(studentRepository.existsByEmail(testStudentDTO.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            studentService.createStudent(testStudentDTO);
        });

        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void getStudentById_Success() {
        when(studentRepository.findById("507f1f77bcf86cd799439011")).thenReturn(Optional.of(testStudent));
        when(gradeRepository.findByStudentId(any())).thenReturn(java.util.Collections.emptyList());

        StudentDTO result = studentService.getStudentById("507f1f77bcf86cd799439011");

        assertNotNull(result);
        assertEquals(testStudent.getEmail(), result.getEmail());
    }

    @Test
    void deleteStudent_Success() {
        when(studentRepository.findById("507f1f77bcf86cd799439011")).thenReturn(Optional.of(testStudent));

        studentService.deleteStudent("507f1f77bcf86cd799439011");

        verify(studentRepository, times(1)).delete(testStudent);
    }
}
