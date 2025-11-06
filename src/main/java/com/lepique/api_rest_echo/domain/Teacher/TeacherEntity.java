package com.lepique.api_rest_echo.domain.Teacher;

import com.lepique.api_rest_echo.domain.CourseTeacher.CourseTeacherEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter
@EqualsAndHashCode @ToString
@Table(name = "TEACHERS")
public class TeacherEntity {
    @Id
    @Column(name = "TEACHER_CODE", length = 10)
    private String teacherCode;

    @Column(name = "NIT", nullable = false, length = 10, unique = true)
    private String nit;

    @Column(name = "FIRSTNAME", nullable = false, length = 225)
    private String firstName;

    @Column(name = "LASTNAME", nullable = false, length = 350)
    private String lastName;

    @Column(name = "BIRTHDAY", nullable = false)
    private Date birthday;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<CourseTeacherEntity> teachingAssignments;
}
