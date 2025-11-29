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
        try {
            log.info("========================================");
            log.info("Starting data loading process...");
            log.info("========================================");
            
            if (userRepository.count() == 0) {
                log.info("Loading users...");
                loadUsers();
            } else {
                log.info("Users already exist, skipping user creation");
            }

            if (studentRepository.count() == 0) {
                log.info("Loading students...");
                loadStudents();
            } else {
                log.info("Students already exist, skipping student creation");
            }

            if (courseRepository.count() == 0) {
                log.info("Loading courses...");
                loadCourses();
            } else {
                log.info("Courses already exist, skipping course creation");
            }

            if (gradeRepository.count() == 0) {
                log.info("Loading grades...");
                loadGrades();
            } else {
                log.info("Grades already exist, skipping grade creation");
            }

            log.info("========================================");
            log.info("Sample data loaded successfully!");
            log.info("Total Users: {}", userRepository.count());
            log.info("Total Students: {}", studentRepository.count());
            log.info("Total Courses: {}", courseRepository.count());
            log.info("Total Grades: {}", gradeRepository.count());
            log.info("Admin credentials: admin / admin123");
            log.info("User credentials: user / user123");
            log.info("========================================");
        } catch (Exception e) {
            log.error("========================================");
            log.error("ERROR loading sample data: {}", e.getMessage(), e);
            log.error("========================================");
        }
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

        // Indian student names with diverse backgrounds
        students.add(Student.builder()
                .firstName("Aarav").lastName("Sharma")
                .email("aarav.sharma@university.edu")
                .phoneNumber("+91-9876543210")
                .enrollmentId("STU001")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Priya").lastName("Patel")
                .email("priya.patel@university.edu")
                .phoneNumber("+91-9876543211")
                .enrollmentId("STU002")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Arjun").lastName("Kumar")
                .email("arjun.kumar@university.edu")
                .phoneNumber("+91-9876543212")
                .enrollmentId("STU003")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Ananya").lastName("Singh")
                .email("ananya.singh@university.edu")
                .phoneNumber("+91-9876543213")
                .enrollmentId("STU004")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Vivaan").lastName("Reddy")
                .email("vivaan.reddy@university.edu")
                .phoneNumber("+91-9876543214")
                .enrollmentId("STU005")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Diya").lastName("Gupta")
                .email("diya.gupta@university.edu")
                .phoneNumber("+91-9876543215")
                .enrollmentId("STU006")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Aditya").lastName("Verma")
                .email("aditya.verma@university.edu")
                .phoneNumber("+91-9876543216")
                .enrollmentId("STU007")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Isha").lastName("Mehta")
                .email("isha.mehta@university.edu")
                .phoneNumber("+91-9876543217")
                .enrollmentId("STU008")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Reyansh").lastName("Joshi")
                .email("reyansh.joshi@university.edu")
                .phoneNumber("+91-9876543218")
                .enrollmentId("STU009")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Saanvi").lastName("Nair")
                .email("saanvi.nair@university.edu")
                .phoneNumber("+91-9876543219")
                .enrollmentId("STU010")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Kabir").lastName("Desai")
                .email("kabir.desai@university.edu")
                .phoneNumber("+91-9876543220")
                .enrollmentId("STU011")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Myra").lastName("Chopra")
                .email("myra.chopra@university.edu")
                .phoneNumber("+91-9876543221")
                .enrollmentId("STU012")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Vihaan").lastName("Iyer")
                .email("vihaan.iyer@university.edu")
                .phoneNumber("+91-9876543222")
                .enrollmentId("STU013")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Aanya").lastName("Malhotra")
                .email("aanya.malhotra@university.edu")
                .phoneNumber("+91-9876543223")
                .enrollmentId("STU014")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Ayaan").lastName("Rao")
                .email("ayaan.rao@university.edu")
                .phoneNumber("+91-9876543224")
                .enrollmentId("STU015")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Kiara").lastName("Bhatia")
                .email("kiara.bhatia@university.edu")
                .phoneNumber("+91-9876543225")
                .enrollmentId("STU016")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Rohan").lastName("Agarwal")
                .email("rohan.agarwal@university.edu")
                .phoneNumber("+91-9876543226")
                .enrollmentId("STU017")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Navya").lastName("Srinivasan")
                .email("navya.srinivasan@university.edu")
                .phoneNumber("+91-9876543227")
                .enrollmentId("STU018")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Krishiv").lastName("Shah")
                .email("krishiv.shah@university.edu")
                .phoneNumber("+91-9876543228")
                .enrollmentId("STU019")
                .active(true).build());

        students.add(Student.builder()
                .firstName("Zara").lastName("Khan")
                .email("zara.khan@university.edu")
                .phoneNumber("+91-9876543229")
                .enrollmentId("STU020")
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

        String[] assessments = {"Midterm Exam", "Final Exam", "Quiz 1", "Quiz 2", "Quiz 3", 
                               "Assignment 1", "Assignment 2", "Assignment 3", "Project", "Presentation"};

        for (Student student : students) {
            // Each student gets 4-6 grades in different courses for better statistics
            int numGrades = 4 + random.nextInt(3);
            List<Course> selectedCourses = new ArrayList<>(courses);
            java.util.Collections.shuffle(selectedCourses);

            for (int i = 0; i < numGrades && i < selectedCourses.size(); i++) {
                Course course = selectedCourses.get(i);
                
                // Generate realistic grade distribution:
                // 20% A (90-100), 30% B (80-89), 30% C (70-79), 15% D (60-69), 5% F (0-59)
                double rand = random.nextDouble();
                double score;
                
                if (rand < 0.20) {
                    score = 90 + random.nextDouble() * 10; // A grade
                } else if (rand < 0.50) {
                    score = 80 + random.nextDouble() * 10; // B grade
                } else if (rand < 0.80) {
                    score = 70 + random.nextDouble() * 10; // C grade
                } else if (rand < 0.95) {
                    score = 60 + random.nextDouble() * 10; // D grade
                } else {
                    score = 40 + random.nextDouble() * 20; // F grade
                }
                
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
