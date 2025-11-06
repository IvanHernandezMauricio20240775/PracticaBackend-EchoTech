package com.lepique.api_rest_echo.service.Students;

import com.lepique.api_rest_echo.domain.Grade.GradeEntity;
import com.lepique.api_rest_echo.domain.Students.StudentsEntity;
import com.lepique.api_rest_echo.models.DTO.Students.StudentsDTO;
import com.lepique.api_rest_echo.repository.Grade.GradeRepository;
import com.lepique.api_rest_echo.repository.Students.StudentsRepository;
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
public class StudentsService {

    @Autowired
    private StudentsRepository accesStudentsRepo;

    @Autowired
    private GradeRepository accesGradeRepo;


    public List<StudentsDTO> GetAllStudents(){
        List<StudentsEntity> students = accesStudentsRepo.findAll();

        return students.stream()
                .map(this::convertStudentsToDTO)
                .collect(Collectors.toList());
    }

    public StudentsDTO getStudentByCode(String code){
        Optional<StudentsEntity> studentOpt = accesStudentsRepo.findById(code);
        if(studentOpt.isPresent()){
            return convertStudentsToDTO(studentOpt.get());
        }else {
            log.error("Error al filtrar estudiante por codigo");
            throw new EntityNotFoundException("No existe el estudiante con codigo: " + code);
        }
    }

    public List<StudentsDTO> GetStudentsByGradeId(Long ID_Grade){

        GradeEntity grade = accesGradeRepo.findById(ID_Grade)
                .orElseThrow(() -> {
                    log.error("No se encontró ningún grado con el ID: {}", ID_Grade);
                    return new EntityNotFoundException("No existe el grado con ID: " + ID_Grade);
                });

        // Buscamos los estudiantes por la entidad Grade (osea el grado)
        List<StudentsEntity> students = accesStudentsRepo.findStudentByGrade(grade);
        //Retornamos la lista y la convertimos a DTO
        return students.stream()
                .map(this::convertStudentsToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public StudentsDTO CreateNewStudent(StudentsDTO dto){
        //Doble validacion
        if(dto.getCode() == null || dto.getCode().isEmpty() ||
                dto.getFullName() == null || dto.getFullName().isEmpty() ||
                dto.getAge() == null ||
                dto.getIdGrade() == null){

            throw new IllegalArgumentException("Los campos no deben de ir vacios");
        }
        if(accesStudentsRepo.existsById(dto.getCode())){
            log.warn("El Codigo ya existe");
            throw new EntityExistsException("El Estudiante con el codigo:" + dto.getCode() + " Ya está registrado");
        }
        try {
            StudentsEntity student = convertStudentToEntity(dto);
            StudentsEntity studentSave = accesStudentsRepo.save(student);
            return convertStudentsToDTO(studentSave);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al intentar insertar el estudiante", e);
            throw new RuntimeException("Error inesperado en la base de datos al guardar: " + e.getMessage());
        }

    }

    @Transactional
    public StudentsDTO UpdateStudent(String code, StudentsDTO dto){
        if(code.isEmpty() || dto.getFullName().isEmpty() || dto.getAge() == null || dto.getIdGrade() == null){
            log.warn("El nombre, codigo, edad, y grado no deben de ir vacios");
            throw new IllegalArgumentException("Los campos no deben de ir vacios");
        }
        if(!accesStudentsRepo.existsById(code)) {
            log.warn("El Codigo del Estudiante no existe");
            throw new EntityExistsException("El Estudiante con el codigo:" + code + " no existe");
        }
        try {
            StudentsEntity Student = new StudentsEntity();
            Student.setCode(code);
            Student.setFullName(dto.getFullName());
            Student.setAge(dto.getAge());
            GradeEntity grade = accesGradeRepo.findById(dto.getIdGrade())
                    .orElseThrow(() -> new EntityNotFoundException("No se encontro ningun grado con ese ID"));
            Student.setGrade(grade);

            StudentsEntity StudentUpdated = accesStudentsRepo.save(Student);
            return convertStudentsToDTO(StudentUpdated);
        }catch (Exception e){
            log.error("No se logro actualizar el Estudiante");
            throw new EntityNotFoundException("Error al Actualizar la informacion del Estudiante" + e.getMessage());
        }
    }

    public boolean deleteStudent (String code){
        try{
            if(accesStudentsRepo.existsById(code)){
                accesStudentsRepo.deleteById(code);
                log.info("Estudiante con el codigo" + code + " a sido eliminado exitosamente");
                return true;
            }else {
                log.warn("El Estudiante con el codigo" + code + " No existe");
                return false;
            }
        }catch (Exception e){
            log.error("No se logro eliminar el Estudiante");
            throw new EmptyResultDataAccessException("Error al eliminar el Estudiante", -1);
        }
    }

    private StudentsDTO convertStudentsToDTO(StudentsEntity entity){
        StudentsDTO dto = new StudentsDTO();
        dto.setCode(entity.getCode());
        dto.setFullName(entity.getFullName());
        dto.setAge(entity.getAge());
        if(entity.getGrade() != null){
            dto.setIdGrade(entity.getGrade().getIdGrade());
            dto.setGrade(entity.getGrade().getName());
        }
        return dto;
    }

    private  StudentsEntity convertStudentToEntity(StudentsDTO dto){
        StudentsEntity entity = new StudentsEntity();
        entity.setCode(dto.getCode());
        entity.setFullName(dto.getFullName());
        entity.setAge(dto.getAge());

        GradeEntity grade = accesGradeRepo.findById(dto.getIdGrade())
                .orElseThrow(() -> new EntityNotFoundException("No se encontro ningun grado con ese ID"));

        entity.setGrade(grade);
        return  entity;
    }
}
