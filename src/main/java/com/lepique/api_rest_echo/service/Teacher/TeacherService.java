package com.lepique.api_rest_echo.service.Teacher;

import com.lepique.api_rest_echo.domain.Course.CourseEntity;
import com.lepique.api_rest_echo.domain.CourseTeacher.CourseTeacherEntity;
import com.lepique.api_rest_echo.domain.Grade.GradeEntity;
import com.lepique.api_rest_echo.domain.Students.StudentsEntity;
import com.lepique.api_rest_echo.domain.Teacher.TeacherEntity;
import com.lepique.api_rest_echo.models.DTO.Course.CourseDTO;
import com.lepique.api_rest_echo.models.DTO.CourseTeacher.CourseTeacherDTO;
import com.lepique.api_rest_echo.models.DTO.Students.StudentsDTO;
import com.lepique.api_rest_echo.models.DTO.Teacher.TeacherDTO;
import com.lepique.api_rest_echo.repository.Course.CourseRepository;
import com.lepique.api_rest_echo.repository.CourseTeacher.CourseTeacherRepository;
import com.lepique.api_rest_echo.repository.Teacher.TeacherRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TeacherService {

    @Autowired
    private TeacherRepository accessTeacherRepo;

    @Autowired
    private CourseRepository accessCourseRepo;

    @Autowired
    private CourseTeacherRepository accessCourseTeacherRepo;


    @Autowired
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<TeacherDTO> getAllTeacher(){

        entityManager.clear();
        List<TeacherEntity> teacherList = accessTeacherRepo.findAllWithAssignments();

        return teacherList.stream()
                .map(this::converTeacherToDTO)
                .collect(Collectors.toList());
    }

    public TeacherDTO getTeacherByCode(String code){
        Optional<TeacherEntity> teacherOpt = accessTeacherRepo.findById(code);
        if(teacherOpt.isPresent()){
            return converTeacherToDTO(teacherOpt.get());
        }else {
            log.error("Error al filtrar al Docente por codigo");
            throw new EntityNotFoundException("No existe el Docente con codigo: " + code);
        }
    }


    public TeacherDTO createNewTeacher(TeacherDTO dto){
        if(dto == null || dto.getTeacherCode().isEmpty() || dto.getNit().isEmpty()|| dto.getLastName().isEmpty()
        || dto.getBirthday() == null){
            log.warn("Campos Vacios, el nombre, apellido, codigo, nit, o fecha de nacimiento no debe de ir vacio ");
            throw new IllegalArgumentException("Los campos no deben de ir vacios");
        }
        if(accessTeacherRepo.existsById(dto.getTeacherCode())){
            log.warn("El Codigo del docente ya existe");
            throw new EntityExistsException("El Docente con el codigo:" + dto.getTeacherCode() + " Ya está registrado");
        }
        if(accessTeacherRepo.existsById(dto.getNit())){
            log.warn("El Nit del docente ya existe");
            throw new EntityExistsException("El Docente con el NIT:" + dto.getNit() + " Ya está registrado");
        }
        try{
            TeacherEntity createTeacher = convertTeacherToEntity(dto);
            TeacherEntity savedTeacher = accessTeacherRepo.save(createTeacher);
            return converTeacherToDTO(savedTeacher);
        }catch (Exception e){
            log.error("Error al crear el Docente: "+ e);
            throw new EntityNotFoundException("Error al insertar el Docente");
        }
    }

    @Transactional
    public TeacherDTO addTeacherToCourse(CourseTeacherDTO dto){
        if(dto.getIdCourse() == null || dto.getTeacherCode().isEmpty()){
            log.warn("Campos Vacios, el ID del curso, y el codigo del Docente no debe de ir vacio ");
            throw new IllegalArgumentException("Los campos no deben de ir vacios");
        }
        try{
            //Verificamos la existencia tanto del docente como el curso
            TeacherEntity teacher = accessTeacherRepo.findById(dto.getTeacherCode())
                    .orElseThrow(() -> new EntityNotFoundException("No existe docente con código: " + dto.getTeacherCode()));
            CourseEntity course = accessCourseRepo.findById(dto.getIdCourse())
                    .orElseThrow(() -> new EntityNotFoundException("No existe curso con ID: " + dto.getIdCourse()));

            //Validamos que este docente no se le haya asignado ya a un curso anteriormente
            boolean alreadyAssigned = accessCourseTeacherRepo.findAll().stream()
                    .anyMatch(CourseTeacher -> CourseTeacher.getTeacher().getTeacherCode().equals(teacher.getTeacherCode())
                            && CourseTeacher.getCourse().getId().equals(course.getId()));
            if (alreadyAssigned) {
                log.warn("El docente ya fue asignado a ese curso");
                throw new EntityExistsException("El docente ya está asignado a ese curso.");
            }

            CourseTeacherEntity teacherAdded = convertTeacherToCourseToEntity(dto);
            //Inscribimos al Teacher en ese curso
            CourseTeacherEntity teacherSaved = accessCourseTeacherRepo.save(teacherAdded);
            entityManager.clear();
            //Retornamos la informacion del profesor pero ahora con los cursos que se le han asignado
            TeacherEntity reloadTeacher = accessTeacherRepo.findByTeacherWithAssignments(dto.getTeacherCode())
                    .orElseThrow(() -> new EntityNotFoundException("No se encontro ningun Docente con ese Codigo"));
            return converTeacherToDTO(reloadTeacher);
        }catch (Exception e){
            log.error("Error al registrar al Docente en un Curso: "+ e);
            throw new EntityNotFoundException("Error al registrar al Docente en un Curso" + e.getMessage());
        }
    }

    @Transactional
    public void deleteTeacherAssignmentByCodes(String teacherCode, Long courseId) {
        if (teacherCode == null || teacherCode.trim().isEmpty() || courseId == null) {
            throw new IllegalArgumentException("El código del docente y el ID del curso son obligatorios.");
        }

        int deletedRows = accessCourseTeacherRepo.deleteAssignmentByCodes(teacherCode, courseId);
        entityManager.clear();
        if (deletedRows == 0) {
            log.warn("Intento fallido de eliminar la asignación. Docente: {} Curso ID: {}", teacherCode, courseId);
            throw new EntityNotFoundException("No se encontró la asignación del docente " + teacherCode + " al curso con ID " + courseId);
        }
        log.info("Asignación eliminada exitosamente. Docente: {} Curso ID: {}", teacherCode, courseId);
    }

    public TeacherDTO updateTeacher(String CodeTeacher, TeacherDTO dto){
        if(CodeTeacher.isEmpty() || dto.getNit().isEmpty()|| dto.getLastName().isEmpty()
                || dto.getBirthday() == null){
            log.warn("Campos Vacios, el nombre, apellido, codigo, nit, o fecha de nacimiento no debe de ir vacio ");
            throw new IllegalArgumentException("Los campos no deben de ir vacios");
        }
        if(!accessTeacherRepo.existsById(CodeTeacher)) {
            log.warn("El Codigo del docente no existe");
            throw new EntityExistsException("El Docente con el codigo:" + dto.getTeacherCode() + " no existe");
        }
        try{
            TeacherEntity teacher = new TeacherEntity();
            teacher.setTeacherCode(CodeTeacher);
            teacher.setNit(dto.getNit());
            teacher.setFirstName(dto.getFirstName());
            teacher.setLastName(dto.getLastName());
            teacher.setBirthday(dto.getBirthday());

            TeacherEntity updatedTeacher = accessTeacherRepo.save(teacher);
            return converTeacherToDTO(updatedTeacher);
        }catch (Exception e){
            log.error("No se logro actualizar el Docente");
            throw new EntityNotFoundException("Error al Actualizar la informacion del Docente" + e.getMessage());
        }
    }

    public boolean deleteTeacher (String code){
        try{
            if(accessTeacherRepo.existsById(code)){
                accessTeacherRepo.deleteById(code);
                log.info("Docente con el codigo" + code + " a sido eliminado exitosamente");
                return true;
            }else {
                log.warn("El Docente con el codigo" + code + " No existe");
                return false;
            }
        }catch (Exception e){
            log.error("No se logro eliminar el Docente");
            throw new EmptyResultDataAccessException("Error al eliminar el Docente", -1);
        }
    }

    private CourseTeacherEntity convertTeacherToCourseToEntity(CourseTeacherDTO dto){

        CourseTeacherEntity entity = new CourseTeacherEntity();

        TeacherEntity teacher = accessTeacherRepo.findById(dto.getTeacherCode())
                .orElseThrow(() -> new EntityNotFoundException("No se encontro ningun Docente con ese Codigo"));
        entity.setTeacher(teacher);
        CourseEntity course = accessCourseRepo.findById(dto.getIdCourse())
                .orElseThrow(() -> new EntityNotFoundException("No se encontro ningun Curso con ese ID"));
        entity.setCourse(course);

        return  entity;

    }

    private TeacherEntity convertTeacherToEntity(TeacherDTO dto){
        TeacherEntity entity = new TeacherEntity();
        entity.setTeacherCode(dto.getTeacherCode());
        entity.setNit(dto.getNit());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setBirthday(dto.getBirthday());


        return entity;
    }

    private TeacherDTO converTeacherToDTO(TeacherEntity entity){
        TeacherDTO dto = new TeacherDTO();

        dto.setTeacherCode(entity.getTeacherCode());
        dto.setNit(entity.getNit());
        dto.setFirstName(entity.getFirstName());
        dto.setLastName(entity.getLastName());
        dto.setBirthday(entity.getBirthday());

        if (entity.getTeachingAssignments() != null && !entity.getTeachingAssignments().isEmpty()) {

            List<String> courseNames = entity.getTeachingAssignments().stream()
                    .map(assignment -> assignment.getCourse().getName()) // <--- Aquí se necesita el 'CourseEntity' cargado
                    .collect(Collectors.toList());

            dto.setAssignedCourseNames(courseNames);
        } else {
            dto.setAssignedCourseNames(Collections.emptyList());
        }
        return dto;
    }
}
