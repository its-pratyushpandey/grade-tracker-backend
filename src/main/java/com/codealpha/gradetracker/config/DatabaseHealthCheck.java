package com.codealpha.gradetracker.config;

import com.codealpha.gradetracker.repository.CourseRepository;
import com.codealpha.gradetracker.repository.GradeRepository;
import com.codealpha.gradetracker.repository.StudentRepository;
import com.codealpha.gradetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DatabaseHealthCheck {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final GradeRepository gradeRepository;

    @Bean
    @Order(1)
    public CommandLineRunner checkDatabaseConnection() {
        return args -> {
            log.info("========================================");
            log.info("   DATABASE HEALTH CHECK");
            log.info("========================================");
            
            try {
                // Test database connection by counting documents
                long userCount = userRepository.count();
                long studentCount = studentRepository.count();
                long courseCount = courseRepository.count();
                long gradeCount = gradeRepository.count();
                
                log.info("✅ MongoDB connection successful!");
                log.info("📊 Current database statistics:");
                log.info("   - Users: {}", userCount);
                log.info("   - Students: {}", studentCount);
                log.info("   - Courses: {}", courseCount);
                log.info("   - Grades: {}", gradeCount);
                log.info("========================================");
                
                if (studentCount == 0 || courseCount == 0) {
                    log.warn("⚠️  Database appears to be empty. Sample data will be loaded.");
                }
                
            } catch (Exception e) {
                log.error("========================================");
                log.error("❌ DATABASE CONNECTION FAILED!");
                log.error("Error: {}", e.getMessage());
                log.error("Please check your MongoDB URI and network connection");
                log.error("========================================");
                throw e;
            }
        };
    }
}
