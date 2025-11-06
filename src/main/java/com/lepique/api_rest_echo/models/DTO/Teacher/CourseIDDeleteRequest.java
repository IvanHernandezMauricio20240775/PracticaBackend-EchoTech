package com.lepique.api_rest_echo.models.DTO.Teacher;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@EqualsAndHashCode @ToString
public class CourseIDDeleteRequest {
    @JsonProperty("ID_COURSE")
    @NotNull(message = "El ID del curso es obligatorio para la eliminación.")
    private Long idCourse;
}
