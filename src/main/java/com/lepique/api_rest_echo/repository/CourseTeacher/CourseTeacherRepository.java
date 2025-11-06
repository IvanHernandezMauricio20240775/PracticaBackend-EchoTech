package com.lepique.api_rest_echo.repository.CourseTeacher;

import com.lepique.api_rest_echo.domain.CourseTeacher.CourseTeacherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CourseTeacherRepository extends JpaRepository<CourseTeacherEntity, Long> {

    List<CourseTeacherEntity> findByTeacher_TeacherCode(String teacherCode);

    @Modifying
    @Query("DELETE FROM CourseTeacherEntity cte WHERE cte.teacher.teacherCode = :teacherCode AND cte.course.id = :courseId")
    int deleteAssignmentByCodes(@Param("teacherCode") String teacherCode, @Param("courseId") Long courseId);
}
