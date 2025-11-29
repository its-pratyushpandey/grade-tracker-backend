package com.codealpha.gradetracker.repository;

import com.codealpha.gradetracker.model.Grade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GradeRepository extends MongoRepository<Grade, String> {

    List<Grade> findByStudentId(String studentId);

    List<Grade> findByCourseId(String courseId);

    Page<Grade> findByStudentId(String studentId, Pageable pageable);

    @Query("{ 'studentId': ?0, 'courseId': ?1 }")
    List<Grade> findByStudentIdAndCourseId(String studentId, String courseId);

    @Query(value = "{ 'studentId': ?0 }", fields = "{ 'numericScore': 1 }")
    List<Grade> findScoresByStudentId(String studentId);

    @Query(value = "{ 'courseId': ?0 }", fields = "{ 'numericScore': 1 }")
    List<Grade> findScoresByCourseId(String courseId);

    @Query(value = "{}", fields = "{ 'numericScore': 1 }")
    List<Grade> findAllScores();

    @Query("{ 'gradeDate': { $gte: ?0, $lte: ?1 } }")
    List<Grade> findByGradeDateBetween(LocalDate startDate, LocalDate endDate);

    @Query(value = "{}", count = true)
    long countAllGrades();

    @Query("{ 'numericScore': { $lt: 60 } }")
    List<Grade> findFailingGrades();

    @Query(value = "{}", fields = "{ 'numericScore': 1 }", sort = "{ 'numericScore': 1 }")
    List<Grade> findAllScoresOrdered();
}
