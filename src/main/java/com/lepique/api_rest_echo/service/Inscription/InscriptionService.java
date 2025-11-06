package com.lepique.api_rest_echo.service.Inscription;

import com.lepique.api_rest_echo.domain.Course.CourseEntity;
import com.lepique.api_rest_echo.domain.Inscription.InscriptionsEntity;
import com.lepique.api_rest_echo.domain.Students.StudentsEntity;
import com.lepique.api_rest_echo.domain.Teacher.TeacherEntity;
import com.lepique.api_rest_echo.models.DTO.Inscription.InscriptionDTO;
import com.lepique.api_rest_echo.models.DTO.Inscription.ScoreByCourseDTO;
import com.lepique.api_rest_echo.repository.Course.CourseRepository;
import com.lepique.api_rest_echo.repository.Inscription.InscriptionRepository;
import com.lepique.api_rest_echo.repository.Students.StudentsRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InscriptionService {

    @Autowired
    private InscriptionRepository accessInscriptionRepo;

    @Autowired
    private StudentsRepository accessStudentRepo;

    @Autowired
    private CourseRepository accessCourseRepo;

    public List<InscriptionDTO> getAllInscription(){

        List<InscriptionsEntity> list = accessInscriptionRepo.findAll();

        return list.stream()
                .map(this::convertInscriptionToDTO)
                .collect(Collectors.toList());
    }

    public InscriptionDTO inscriptionStudentToCourse(InscriptionDTO dto){
        if(dto.getCode().isEmpty() || dto.getIdCourse() == null){
            log.error("Campos vacios, el Codigo del estudiante y el ID del curso no debe de ir vacio");
            throw new IllegalArgumentException("Error al Inscribir al estudiante, el ID o el Codigo no deben de venir vacios");
        }
        try{
            //Verificamos la existencia tanto del Estudiante como el curso
            StudentsEntity students = accessStudentRepo.findById(dto.getCode())
                    .orElseThrow(() -> new EntityNotFoundException("No existe El Estudiante con código: " + dto.getCode()));
            CourseEntity course = accessCourseRepo.findById(dto.getIdCourse())
                    .orElseThrow(() -> new EntityNotFoundException("No existe curso con ID: " + dto.getIdCourse()));

            //Validamos que este Estudiante no se le haya Inscrito ya a ese curso anteriormente
            boolean alreadyInscribe = accessInscriptionRepo.findAll().stream()
                    .anyMatch(Inscription -> Inscription.getStudent().getCode().equals(students.getCode())
                            && Inscription.getCourse().getId().equals(course.getId()));
            if (alreadyInscribe) {
                log.warn("El Estudiante ya fue inscrito a ese curso");
                throw new EntityExistsException("El Estudainte ya está inscrito a ese curso.");
            }

            InscriptionsEntity Inscription = convertInscriptionToEntity(dto);
            InscriptionsEntity SaveInscription = accessInscriptionRepo.save(Inscription);
            return convertInscriptionToDTO(SaveInscription);
        }catch (Exception e){
            log.error("Error al registrar al Estudiante en un Curso: "+ e);
            throw new EntityNotFoundException("Error al registrar al Estudiante en un Curso" + e.getMessage());
        }
    }

    @Transactional
    public InscriptionDTO updateScore(Long InscriptionID, BigDecimal newScore) {
        if (newScore == null) {
            throw new IllegalArgumentException("La calificación no puede ser nula.");
        }
        if (newScore.compareTo(BigDecimal.ZERO) < 0 || newScore.compareTo(new BigDecimal("10.00")) > 0) {
            throw new IllegalArgumentException("La calificación debe estar entre 0.00 y 10.00.");
        }
        InscriptionsEntity inscription = accessInscriptionRepo.findById(InscriptionID)
                .orElseThrow(() -> new EntityNotFoundException("Inscripción no encontrada con ID: " + InscriptionID));

        inscription.setFinallyScore(newScore);
        InscriptionsEntity updatedInscription = accessInscriptionRepo.save(inscription);
        return convertInscriptionToDTO(updatedInscription);
    }

    @Transactional(readOnly = true)
    public List<InscriptionDTO> getStudentsByCourse(Long idCourse) {
        if (idCourse == null) {
            throw new IllegalArgumentException("El ID del curso no puede ser nulo.");
        }

        List<InscriptionsEntity> assignments = accessInscriptionRepo.findByCourse_Id(idCourse);
        if (assignments.isEmpty()) {
            log.warn("No se encontraron estudiantes inscritos para el curso con ID: {}", idCourse);
            throw new EntityNotFoundException("No se encontraron estudiantes inscritos para el curso con ID: " + idCourse);
        }

        return assignments.stream()
                .map(this::convertInscriptionToDTO)
                .collect(Collectors.toList());
    }

    public List<ScoreByCourseDTO> getScoreByStudentCode(String studentCode) {
        if (studentCode.trim().isEmpty()) {
            throw new IllegalArgumentException("El código del estudiante no puede ser nulo o vacío.");
        }
        List<InscriptionsEntity> inscriptions = accessInscriptionRepo.findByStudent_Code(studentCode);
        if (inscriptions.isEmpty()) {
            log.warn("No se encontraron inscripciones para el estudiante con código: {}", studentCode);
            return Collections.emptyList();
        }

        return inscriptions.stream()
                .map(this::convertScoreByStudentToDTO)
                .collect(Collectors.toList());
    }

    //Solo retornaremos el Promedio por ello no ocupare de un DTO
    public BigDecimal getStudentAveragesByCourse(String studentCode) {
        if (studentCode.trim().isEmpty()) {
            throw new IllegalArgumentException("El código del estudiante no puede ser nulo o vacío.");
        }
        List<InscriptionsEntity> inscriptions = accessInscriptionRepo.findByStudent_Code(studentCode);
        if (inscriptions.isEmpty()) {
            return BigDecimal.ZERO;
        }

        List<BigDecimal> scores = inscriptions.stream()
                .map(InscriptionsEntity::getFinallyScore)
                .filter(score -> score != null && score.compareTo(BigDecimal.ZERO) > 0) //Filtramos las calificaciones que no vengan vacias y sean mayores que 0
                .collect(Collectors.toList());

        //Validamos que las calificaciones no vengan vacias y si existen las inscripcciones pero no hay calificaciones mayores que 0 retornamos un 0
        if (scores.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = scores.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add); //Calculamos la suma de las calificaciones
        BigDecimal count = new BigDecimal(scores.size()); //Obtenemos cuantas calificaciones se sumaran
        return sum.divide(count, 2, RoundingMode.HALF_UP); //Realizo la Division de la suma de las calificaciones entre la cantidad de calificaciones sumadas para obtener el promedio
        // Utilizamos la escala '2' para definir el número de decimales (0.00)
        //Utilizamos RoundingMode para redondear el promedio y que no exceda el resultado a la escala ya que al dividir dos decimales el resultado puede llegar a ser infinito
    }

    @Transactional(readOnly = true)
    public BigDecimal getCourseAverageScore(Long idCourse) {
        if (idCourse == null) {
            throw new IllegalArgumentException("El ID del curso no puede ser nulo.");
        }

        List<InscriptionsEntity> inscriptions = accessInscriptionRepo.findByCourse_Id(idCourse);
        if (inscriptions.isEmpty()) {
            throw new EntityNotFoundException("No se encontraron inscripciones para el curso con ID: " + idCourse);
        }

        List<BigDecimal> scores = inscriptions.stream()
                .map(InscriptionsEntity::getFinallyScore)
                .filter(score -> score != null && score.compareTo(BigDecimal.ZERO) > 0) //Filtramos las calificaciones que no sean nulas ni que sean menores que 0
                .collect(Collectors.toList());

        if (scores.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = scores.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal count = new BigDecimal(scores.size());
        return sum.divide(count, 2, RoundingMode.HALF_UP);
    }


    public boolean unsubscribeTheStudent (Long ID_Inscription){
        if(!accessInscriptionRepo.existsById(ID_Inscription)){
            throw new EntityNotFoundException("No existe ninguna inscripción con el ID: " + ID_Inscription);
        }
        try{
            accessInscriptionRepo.deleteById(ID_Inscription);
            log.info("El Estudiante ha sido desuscribido del curso exitosamente. ID: {}", ID_Inscription);
            return true;
        }catch (Exception e){
            log.error("No se logró desuscribir el Estudiante con ID: {}", ID_Inscription, e);
            throw e;
        }
    }

    private ScoreByCourseDTO convertScoreByStudentToDTO(InscriptionsEntity entity){
        ScoreByCourseDTO  dto = new ScoreByCourseDTO();
        if(entity.getCourse() != null){
            dto.setCourseName(entity.getCourse().getName());
        }
        dto.setFinallyScore(entity.getFinallyScore());
        return dto;
    }
    private InscriptionsEntity convertInscriptionToEntity(InscriptionDTO dto){

        InscriptionsEntity entity = new InscriptionsEntity();

        StudentsEntity student = accessStudentRepo.findById(dto.getCode())
                .orElseThrow(() -> new EntityNotFoundException("Error no se encontro ningun estudiante con el Codigo" + dto.getCode()));
        entity.setStudent(student);
        CourseEntity course = accessCourseRepo.findById(dto.getIdCourse())
                .orElseThrow(() -> new EntityNotFoundException("Error no se encontro ningun Curso con el ID" + dto.getIdCourse()));
        entity.setCourse(course);

        return entity;
    }
    private InscriptionDTO convertInscriptionToDTO(InscriptionsEntity entity){

        InscriptionDTO dto = new InscriptionDTO();

        dto.setId(entity.getId());
        if(entity.getStudent() != null){
            dto.setCode(entity.getStudent().getCode());
            dto.setFullName(entity.getStudent().getFullName());
        }
        if(entity.getCourse() != null){
            dto.setIdCourse(entity.getCourse().getId());
            dto.setCourseName(entity.getCourse().getName());
        }
        dto.setInscriptionDate(entity.getInscriptionDate());
        dto.setFinallyScore(entity.getFinallyScore());

        return dto;
    }
}
