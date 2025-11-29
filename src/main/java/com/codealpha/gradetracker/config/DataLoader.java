package com.codealpha.gradetracker.config;

import com.codealpha.gradetracker.model.Course;
import com.codealpha.gradetracker.model.Grade;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.model.User;
import com.codealpha.gradetracker.repository.CourseRepository;
import com.codealpha.gradetracker.repository.GradeRepository;
import com.codealpha.gradetracker.repository.StudentRepository;
import com.codealpha.gradetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final GradeRepository gradeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            loadUsers();
        }

        if (studentRepository.count() == 0) {
            loadStudents();
        }

        if (courseRepository.count() == 0) {
            loadCourses();
        }

        if (gradeRepository.count() == 0) {
            loadGrades();
        }

        log.info("========================================");
        log.info("Sample data loaded successfully!");
        log.info("Admin credentials: admin / admin123");
        log.info("User credentials: user / user123");
        log.info("========================================");
    }

    private void loadUsers() {
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .fullName("Admin User")
                .role(User.Role.ROLE_ADMIN)
                .enabled(true)
                .build();

        User user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("user123"))
                .fullName("Regular User")
                .role(User.Role.ROLE_USER)
                .enabled(true)
                .build();

        userRepository.saveAll(List.of(admin, user));
        log.info("Created 2 users");
    }

    private void loadStudents() {
        List<Student> students = new ArrayList<>();

        students.add(Student.builder()
                .firstName("Emma").lastName("Johnson")
                .email("emma.johnson@university.edu")
                .phoneNumber("555-0101")
                .enrollmentId("STU001")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Liam").lastName("Smith")
                .email("liam.smith@university.edu")
                .phoneNumber("555-0102")
                .enrollmentId("STU002")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Olivia").lastName("Williams")
                .email("olivia.williams@university.edu")
                .phoneNumber("555-0103")
                .enrollmentId("STU003")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Noah").lastName("Brown")
                .email("noah.brown@university.edu")
                .phoneNumber("555-0104")
                .enrollmentId("STU004")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Ava").lastName("Jones")
                .email("ava.jones@university.edu")
                .phoneNumber("555-0105")
                .enrollmentId("STU005")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Ethan").lastName("Garcia")
                .email("ethan.garcia@university.edu")
                .phoneNumber("555-0106")
                .enrollmentId("STU006")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Sophia").lastName("Martinez")
                .email("sophia.martinez@university.edu")
                .phoneNumber("555-0107")
                .enrollmentId("STU007")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Mason").lastName("Davis")
                .email("mason.davis@university.edu")
                .phoneNumber("555-0108")
                .enrollmentId("STU008")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Isabella").lastName("Rodriguez")
                .email("isabella.rodriguez@university.edu")
                .phoneNumber("555-0109")
                .enrollmentId("STU009")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Lucas").lastName("Wilson")
                .email("lucas.wilson@university.edu")
                .phoneNumber("555-0110")
                .enrollmentId("STU010")
                .active(true).build());

        studentRepository.saveAll(students);
        log.info("Created {} students", students.size());
    }

    private void loadCourses() {
        List<Course> courses = new ArrayList<>();

        courses.add(Course.builder()
                .name("Data Structures and Algorithms")
                .code("CS201")
                .description("Introduction to fundamental data structures and algorithms")
                .credits(4)
                .active(true).build());

        courses.add(Course.builder()
                .name("Database Management Systems")
                .code("CS301")
                .description("Principles of database design and SQL")
                .credits(3)
                .active(true).build());

        courses.add(Course.builder()
                .name("Web Development")
                .code("CS350")
                .description("Modern web development with React and Node.js")
                .credits(3)
                .active(true).build());

        courses.add(Course.builder()
                .name("Machine Learning")
                .code("CS450")
                .description("Introduction to machine learning algorithms and applications")
                .credits(4)
                .active(true).build());

        courses.add(Course.builder()
                .name("Software Engineering")
                .code("CS401")
                .description("Software development lifecycle and best practices")
                .credits(3)
                .active(true).build());

        courseRepository.saveAll(courses);
        log.info("Created {} courses", courses.size());
    }

    private void loadGrades() {
        List<Student> students = studentRepository.findAll();
        List<Course> courses = courseRepository.findAll();
        List<Grade> grades = new ArrayList<>();
        Random random = new Random();

        String[] assessments = {"Midterm", "Final", "Quiz 1", "Quiz 2", "Assignment 1", "Assignment 2", "Project"};

        for (Student student : students) {
            // Each student gets 3-5 grades in random courses
            int numGrades = 3 + random.nextInt(3);
            List<Course> selectedCourses = new ArrayList<>(courses);
            java.util.Collections.shuffle(selectedCourses);

            for (int i = 0; i < numGrades && i < selectedCourses.size(); i++) {
                Course course = selectedCourses.get(i);
                
                // Generate realistic grade (bias towards higher scores)
                double score = 60 + random.nextDouble() * 40; // 60-100
                
                Grade grade = Grade.builder()
                        .student(student)
                        .course(course)
                        .numericScore(Math.round(score * 100.0) / 100.0)
                        .gradeDate(LocalDate.now().minusDays(random.nextInt(90)))
                        .assessment(assessments[random.nextInt(assessments.length)])
                        .description("Grade for " + course.getName())
                        .weight(1.0)
                        .build();
                
                grades.add(grade);
            }
        }

        gradeRepository.saveAll(grades);
        log.info("Created {} grades", grades.size());
    }
}
