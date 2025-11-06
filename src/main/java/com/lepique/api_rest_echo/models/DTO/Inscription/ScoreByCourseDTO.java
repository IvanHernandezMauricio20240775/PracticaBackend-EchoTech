package com.lepique.api_rest_echo.models.DTO.Inscription;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter @Setter
@EqualsAndHashCode @ToString
public class ScoreByCourseDTO {

    @JsonProperty("COURSE_NAME")
    private String courseName;

    @JsonProperty("FINALLY_SCORE")
    @DecimalMin(value = "0.00", message = "La calificación final debe ser como mínimo 0.00")
    @DecimalMax(value = "10.00", message = "La calificación final no debe exceder 10.00")
    private BigDecimal finallyScore;
}
