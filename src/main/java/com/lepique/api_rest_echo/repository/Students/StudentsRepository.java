package com.lepique.api_rest_echo.repository.Students;

import com.lepique.api_rest_echo.domain.Grade.GradeEntity;
import com.lepique.api_rest_echo.domain.Students.StudentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentsRepository extends JpaRepository<StudentsEntity, String> {

    //Metodo personalizado para filtrar una lista de estudiantes por un grado en especifico
    List<StudentsEntity> findStudentByGrade(GradeEntity grade);
}
