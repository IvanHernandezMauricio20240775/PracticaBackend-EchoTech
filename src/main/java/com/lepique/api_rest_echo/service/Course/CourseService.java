package com.lepique.api_rest_echo.service.Course;

import com.lepique.api_rest_echo.domain.Course.CourseEntity;
import com.lepique.api_rest_echo.domain.CourseTeacher.CourseTeacherEntity;
import com.lepique.api_rest_echo.domain.Grade.GradeEntity;
import com.lepique.api_rest_echo.domain.Teacher.TeacherEntity;
import com.lepique.api_rest_echo.models.DTO.Course.CourseDTO;
import com.lepique.api_rest_echo.models.DTO.Teacher.TeacherDTO;
import com.lepique.api_rest_echo.repository.Course.CourseRepository;
import com.lepique.api_rest_echo.repository.CourseTeacher.CourseTeacherRepository;
import com.lepique.api_rest_echo.repository.Grade.GradeRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CourseService {

    @Autowired
    private CourseRepository accessCourseRepo;

    @Autowired
    private GradeRepository accessGradeRepo;

    @Autowired
    private CourseTeacherRepository accessCourseTeacherRepo;

    public List<CourseDTO> getAllCourses(){

        List<CourseEntity> list = accessCourseRepo.findAll();

        return list.stream()
                .map(this::ConvertCourseToDTO)
                .collect(Collectors.toList());
    }

    public CourseDTO getCourseByID(Long ID_Course){
        Optional<CourseEntity> courseOpt = accessCourseRepo.findById(ID_Course);
        if(courseOpt.isPresent()){
            return ConvertCourseToDTO(courseOpt.get());
        }else {
            log.error("Error al filtrar al Curso por ID");
            throw new EntityNotFoundException("No existe el Curso con codigo: " + ID_Course);
        }
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByTeacher(String TeacherCode){
        if (TeacherCode.trim().isEmpty()) {
            throw new IllegalArgumentException("El código del profesor no puede ser vacío.");
        }

        List<CourseTeacherEntity> assignments = accessCourseTeacherRepo.findByTeacher_TeacherCode(TeacherCode);

        if (assignments.isEmpty()) {
            log.warn("No se encontraron cursos asignados para el profesor con código: {}", TeacherCode);
            return List.of();
        }

        return assignments.stream()
                .map(assignment -> assignment.getCourse())
                .map(this::ConvertCourseToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CourseDTO createCourse(CourseDTO dto){
        if(dto.getName().isEmpty() || dto.getDescription().isEmpty() || dto.getIdGrade() == null){
          log.warn("Campos Vacios, el nombre, descripcion y el ID grado del curso no debe de ir vacio");
          throw new IllegalArgumentException("Error al Registrar el Curso, hay campos vacios");
        }
        try {
            CourseEntity course = ConvertCourseToEntity(dto);
            CourseEntity savedCourse = accessCourseRepo.save(course);
            return ConvertCourseToDTO(savedCourse);
        }catch (Exception e){
            log.error("Error al intentar insertar el Curso", e);
            throw new RuntimeException("Error inesperado en la base de datos al guardar: " + e.getMessage());
        }
    }

    @Transactional
    public CourseDTO updateCourse(Long ID, CourseDTO dto){
        if(ID == null ||dto.getName().isEmpty() || dto.getDescription().isEmpty() || dto.getIdGrade() == null){
            log.warn("Campos Vacios, El ID En la URL, el nombre, descripcion y el ID grado del curso no debe de ir vacio");
            throw new IllegalArgumentException("Error al Registrar el Curso, hay campos vacios");
        }
        if(!accessCourseRepo.existsById(ID)) {
            log.warn("El Codigo del Curso no existe");
            throw new EntityExistsException("El Curso con el codigo:" + ID + " no existe");
        }
        try{
            CourseEntity course = new CourseEntity();
            course.setId(ID);
            course.setName(dto.getName());
            course.setDescription(dto.getDescription());
            GradeEntity grade = accessGradeRepo.findById(dto.getIdGrade())
                    .orElseThrow(() -> new EntityNotFoundException("No se encontro ningun grado con ese ID"));
            course.setGrade(grade);

            CourseEntity updateCourse = accessCourseRepo.save(course);
            return ConvertCourseToDTO(updateCourse);
        }catch (Exception e){
            log.error("No se logro actualizar el Curso");
            throw new EntityNotFoundException("Error al Actualizar la informacion del Curso" + e.getMessage());
        }
    }

    public boolean deleteCourse (Long ID_Course){
        try{
            if(accessCourseRepo.existsById(ID_Course)){
                accessCourseRepo.deleteById(ID_Course);
                log.info("El Curso con el codigo" + ID_Course + " a sido eliminado exitosamente");
                return true;
            }else {
                log.warn("El Curso con el codigo" + ID_Course + " No existe");
                return false;
            }
        }catch (Exception e){
            log.error("No se logro eliminar el Curso");
            throw new EmptyResultDataAccessException("Error al eliminar el Curso", -1);
        }
    }


    private CourseEntity ConvertCourseToEntity(CourseDTO dto){

        CourseEntity entity = new CourseEntity();

        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());

        GradeEntity grade = accessGradeRepo.findById(dto.getIdGrade())
                .orElseThrow(() -> new EntityNotFoundException("No se encontro ningun grado con ese ID"));
        entity.setGrade(grade);

        return entity;
    }

    private CourseDTO ConvertCourseToDTO(CourseEntity entity){

        CourseDTO dto = new CourseDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setIdGrade(entity.getId());

        if(entity.getGrade() != null){
            dto.setGrade(entity.getGrade().getName());
        }

        return dto;
    }

}
