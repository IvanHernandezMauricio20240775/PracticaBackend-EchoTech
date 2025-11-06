package com.lepique.api_rest_echo.domain.CourseTeacher;

import com.lepique.api_rest_echo.domain.Course.CourseEntity;
import com.lepique.api_rest_echo.domain.Teacher.TeacherEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
@EqualsAndHashCode @ToString
@Table(name = "COURSE_TEACHER")
public class CourseTeacherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDCOURSE_TEACHER")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_COURSE", nullable = false, referencedColumnName = "ID_COURSE")
    private CourseEntity course;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TEACHER_CODE", nullable = false, referencedColumnName = "TEACHER_CODE")
    private TeacherEntity teacher;
}
