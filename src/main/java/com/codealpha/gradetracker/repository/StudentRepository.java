package com.codealpha.gradetracker.repository;

import com.codealpha.gradetracker.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {

    Optional<Student> findByEmail(String email);

    Optional<Student> findByEnrollmentId(String enrollmentId);

    List<Student> findByActiveTrue();

    @Query("{ $or: [ " +
           "{ 'firstName': { $regex: ?0, $options: 'i' } }, " +
           "{ 'lastName': { $regex: ?0, $options: 'i' } }, " +
           "{ 'email': { $regex: ?0, $options: 'i' } }, " +
           "{ 'enrollmentId': { $regex: ?0, $options: 'i' } } " +
           "] }")
    Page<Student> searchStudents(String search, Pageable pageable);

    @Query(value = "{ 'active': true }", count = true)
    long countActiveStudents();

    boolean existsByEmail(String email);

    boolean existsByEnrollmentId(String enrollmentId);
}
