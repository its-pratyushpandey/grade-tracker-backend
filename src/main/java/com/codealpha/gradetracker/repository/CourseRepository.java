package com.codealpha.gradetracker.repository;

import com.codealpha.gradetracker.model.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {

    Optional<Course> findByCode(String code);

    List<Course> findByActiveTrue();

    @Query(value = "{ 'active': true }", count = true)
    long countActiveCourses();

    boolean existsByCode(String code);

    @Query("{ $or: [ " +
           "{ 'name': { $regex: ?0, $options: 'i' } }, " +
           "{ 'code': { $regex: ?0, $options: 'i' } } " +
           "] }")
    List<Course> searchCourses(String search);
}
