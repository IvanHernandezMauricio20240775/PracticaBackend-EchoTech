package com.lepique.api_rest_echo.models.DTO.CourseTeacher;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lepique.api_rest_echo.domain.Course.CourseEntity;
import com.lepique.api_rest_echo.domain.Teacher.TeacherEntity;
import com.lepique.api_rest_echo.models.DTO.Course.CourseDTO;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@EqualsAndHashCode @ToString
public class CourseTeacherDTO {

    @JsonProperty("IDCOURSE_TEACHER")
    private Long idCourseTeacher;

    @JsonProperty("ID_COURSE")
    @NotNull(message = "EL ID del curso no debe de ir vacio")
    private Long idCourse;

    @JsonProperty("TEACHER_CODE")
    @NotBlank(message = "EL codigo del Docente no debe de ir vacio")
    @Size(min = 10, max = 10, message = "EL CODIGO DEL DOCENTE DEBE DE LLEVAR 10 CARACTERES")
    private  String teacherCode;

    @JsonProperty("NAMECOURSE")
    private String courseName;
}
