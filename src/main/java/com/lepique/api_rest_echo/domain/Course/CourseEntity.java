package com.lepique.api_rest_echo.domain.Course;

import com.lepique.api_rest_echo.domain.CourseTeacher.CourseTeacherEntity;
import com.lepique.api_rest_echo.domain.Grade.GradeEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Entity
@Getter @Setter
@EqualsAndHashCode @ToString
@Table(name = "COURSES")
public class CourseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_COURSE")
    private Long id;

    @Column(name = "NAMECOURSE", nullable = false, length = 200)
    private String name;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_GRADE", nullable = false)
    private GradeEntity grade;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CourseTeacherEntity> assignments;

}
