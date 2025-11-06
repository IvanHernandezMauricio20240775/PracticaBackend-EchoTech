package com.lepique.api_rest_echo.domain.Students;

import com.lepique.api_rest_echo.domain.Grade.GradeEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter
@EqualsAndHashCode @ToString
@Table(name = "STUDENTS")
public class StudentsEntity {

    @Id
    @Column(name = "CODE", length = 10)
    private String code;

    @Column(name = "FULLNAME", nullable = false, length = 500)
    private String fullName;

    @Column(name = "AGE", nullable = false)
    private Integer age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_GRADE", nullable = false)
    private GradeEntity grade;
}
