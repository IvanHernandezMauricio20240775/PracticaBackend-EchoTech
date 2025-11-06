package com.lepique.api_rest_echo.models.DTO.Students;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.lepique.api_rest_echo.repository.Students.StudentsOnCreate;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@EqualsAndHashCode @ToString
public class StudentsDTO {

    @JsonProperty("CODE")
    @NotBlank(message = "Para crear el estudiante el Codigo no debe de ir vacio", groups = StudentsOnCreate.class) //Separamos la validacion por grupos4
    @Size(min = 10, max = 10, message = "El código debe tener 10 caracteres.")
    private String code;

    @JsonProperty("FULLNAME")
    @NotBlank(message = "El Nombre Completo del estudiante no debe de ir vacio")
    @Size(min = 2, max = 500, message = "El nombre completo debe tener entre 2 y 500 caracteres.") // Longitud máxima de 500
    private String fullName;

    @JsonProperty("AGE")
    @NotNull(message = "la edad del estudiante no debe de ir vacia")
    private Integer age;

    @JsonProperty("ID_GRADE")
    @NotNull(message = "El ID del grado al que pertenece el estudiante no debe de ir nulo")
    private Long idGrade;

    @JsonProperty("GRADE")
    private String grade;

}
