package com.lepique.api_rest_echo.models.DTO.Inscription;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lepique.api_rest_echo.domain.Course.CourseEntity;
import com.lepique.api_rest_echo.domain.Students.StudentsEntity;
import com.lepique.api_rest_echo.repository.Inscription.InscriptionOnCreate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

@Getter @Setter
@EqualsAndHashCode @ToString
public class InscriptionDTO {
    @JsonProperty("ID_INSCRIPTION")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @JsonProperty("STUDENT_CODE")
    @NotBlank(message = "EL codigo del estudiante no debe de venir vacio")
    @Size(min = 10 , max = 10, message = "El codigo del estudainte debe de lelvar 10 digitos")
    private String code;

    @JsonProperty("COURSE_ID")
    @NotNull(message = "El id del curso al que se inscribiran no debe de regresar nulo")
    @Positive(message = "El id no puede ser negativo")
    private Long idCourse;

    @JsonProperty("STUDENT_NAME")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String fullName;

    @JsonProperty("COURSE_NAME")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String courseName;

    @JsonProperty("INSCRIPTION_DATE")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date inscriptionDate;

    @JsonProperty("FINALLY_SCORE")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Null(message = "LA CALIFICACION NO DEBE DE VENIR AL MOMENTO DE CREAR LA INSCRIPCION", groups = InscriptionOnCreate.class)
    @DecimalMin(value = "0.00", message = "La calificación final debe ser como mínimo 0.00")
    @DecimalMax(value = "10.00", message = "La calificación final no debe exceder 10.00")
    private BigDecimal finallyScore;
}
