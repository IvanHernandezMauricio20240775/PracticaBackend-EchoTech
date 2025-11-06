package com.lepique.api_rest_echo.models.DTO.Course;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lepique.api_rest_echo.domain.Grade.GradeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.bind.annotation.PostMapping;

@Getter @Setter
@EqualsAndHashCode @ToString
public class CourseDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("ID_COURSE")
    private Long id;

    @JsonProperty("NAME_COURSE")
    @NotBlank(message = "El nombre del curso no debe de ir vacio")
    @Size(min = 1, max = 200, message = "El nombre del curso tiene un maximo de 200 caracteres")
    private String name;

    @JsonProperty("DESCRIPTION")
    @Size(max = 500, message = "El maximo de caracteres es de 500")
    private String description;

    @JsonProperty("ID_GRADE")
    @NotNull(message = "El ID del grado no debe de ir nulo")
    @Positive(message = "EL ID DEBE SER POSITIVO")
    private Long idGrade;

    @JsonProperty("GRADE")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String Grade;
}
