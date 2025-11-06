package com.lepique.api_rest_echo.domain.Inscription;

import com.lepique.api_rest_echo.domain.Course.CourseEntity;
import com.lepique.api_rest_echo.domain.Students.StudentsEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter @Setter
@EqualsAndHashCode @ToString
@Table(name = "INSCRIPTIONS")
public class InscriptionsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_INSCRIPTION")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODE", nullable = false)
    private StudentsEntity student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COURSE", nullable = false)
    private CourseEntity course;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "INSCRIPTION_DATE", updatable = false)
    @CreationTimestamp
    private Date inscriptionDate;

    @Column(name = "FINALLY_SCORE")
    private BigDecimal finallyScore = BigDecimal.ZERO;
}
