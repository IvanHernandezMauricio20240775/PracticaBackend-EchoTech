package com.lepique.api_rest_echo.models.DTO.Teacher;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lepique.api_rest_echo.domain.Course.CourseEntity;
import com.lepique.api_rest_echo.domain.CourseTeacher.CourseTeacherEntity;
import com.lepique.api_rest_echo.repository.Teacher.TeacherOnCreate;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Getter @Setter
@EqualsAndHashCode @ToString
public class TeacherDTO {

    @JsonProperty("TEACHER_CODE")
    @NotBlank(message = "El codigo del profesor no debe de ir vacio", groups = TeacherOnCreate.class)
    private String teacherCode;

    @JsonProperty("NIT")
    @NotBlank(message = "El Nit del profesor no debe de ir vacio")
    @Size(min = 10, max = 10, message = "El NIT debe de llevar 10 caracteres incluyendo el - ej: XXXXXXXX-X")
    private String nit;

    @JsonProperty("FIRSTNAME")
    @NotBlank(message = "El primer nombre del profesor no debe de ir vacio")
    @Size(min = 1, max = 225, message = "El maximo es de 225 caracteres para los primeros nombres")
    private String firstName;

    @JsonProperty("LASTNAME")
    @NotBlank(message ="El apellido no puede ir vacio")
    @Size(min = 1, max = 350, message = "El Apellido del profesor debe de llevar maximo 350 caracteres")
    private String lastName;

    @JsonProperty("BIRTHDAY")
    @Past(message = "La fecha de nacimiento unicamente puede ser pasadoa")
    @NotNull(message = "La fecha de nacimiento no puede ser nula.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthday;

    @JsonProperty("ASSIGNED_COURSE")
    private List<String> assignedCourseNames;
}
