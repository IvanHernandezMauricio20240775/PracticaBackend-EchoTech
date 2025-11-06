package com.lepique.api_rest_echo.repository.Teacher;

import com.lepique.api_rest_echo.domain.Teacher.TeacherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<TeacherEntity, String> {

    @Query("SELECT DISTINCT t FROM TeacherEntity t LEFT JOIN FETCH t.teachingAssignments a LEFT JOIN FETCH a.course")
    List<TeacherEntity> findAllWithAssignments();

    @Query("select t from TeacherEntity t left join fetch t.teachingAssignments ta left join fetch ta.course c  where t.teacherCode = :code ")
    Optional<TeacherEntity> findByTeacherWithAssignments(@Param("code") String code);
}
