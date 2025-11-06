package com.lepique.api_rest_echo.controller.Inscription;

import com.lepique.api_rest_echo.models.DTO.Inscription.InscriptionDTO;
import com.lepique.api_rest_echo.models.DTO.Inscription.ScoreByCourseDTO;
import com.lepique.api_rest_echo.models.DTO.Inscription.UpdateScoreRequest;
import com.lepique.api_rest_echo.models.apiResponse.ApiResponse;
import com.lepique.api_rest_echo.repository.Inscription.InscriptionOnCreate;
import com.lepique.api_rest_echo.service.Inscription.InscriptionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/ActionsInscription")
@Slf4j
public class InscriptionController {

    @Autowired
    private InscriptionService accessInscriptionService;

    @Transactional(readOnly = true)
    @GetMapping("/GetAllInscription")
    public ResponseEntity<ApiResponse<List<InscriptionDTO>>> getAllInscription() {
        try {
            List<InscriptionDTO> list = accessInscriptionService.getAllInscription();
            return ResponseEntity.ok(new ApiResponse<>(true, "Inscripcciones Obtenidos Exitosamente", list));
        } catch (Exception e) {
            log.error("Error getAllInscription", e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al obtener las inscripcciones: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //2.2.Crear un endpoint para listar todos los estudiantes inscritos en un curso específico.
    @GetMapping("/GetStudentsByCourse/{ID_Course}")
    public ResponseEntity<ApiResponse<List<InscriptionDTO>>> GetStudentsByCourse(
            @PathVariable("ID_Course") Long ID_Course){
        try {
            List<InscriptionDTO> listStudents = accessInscriptionService.getStudentsByCourse(ID_Course);
            return ResponseEntity.ok(new ApiResponse<>(true, "Estudiantes Obtenidos Exitosamente", listStudents));
        } catch (Exception e) {
            log.error("Error GetStudentsByCourse", e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al obtener los estuddiantes por el Curso: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //3.2.Implementar un endpoint que permita listar las calificaciones de un estudiante en todos
    //los cursos en los que esté inscrito.
    @Transactional(readOnly = true)
    @GetMapping("/GetScoreByStudent/{Code_Student}")
    public ResponseEntity<ApiResponse<List<ScoreByCourseDTO>>> getScoreByStudentCode(
            @PathVariable("Code_Student") String Code_Student){
        try {
            List<ScoreByCourseDTO> listScore = accessInscriptionService.getScoreByStudentCode(Code_Student);
            return ResponseEntity.ok(new ApiResponse<>(true, "Calificaciones Obtenidas Exitosamente", listScore));
        } catch (Exception e) {
            log.error("Error getScoreByStudentCode", e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al obtener las Calificaciones " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //6.1.Implementar un endpoint para obtener el promedio de calificaciones de un estudiante
    //en todos sus cursos.
    @Transactional(readOnly = true)
    @GetMapping("/GetStudentAverageScore/{StudentCode}")
    public ResponseEntity<ApiResponse<BigDecimal>> getStudentAverageScore(
            @PathVariable("StudentCode") String studentCode) {
        try {
            BigDecimal average = accessInscriptionService.getStudentAveragesByCourse(studentCode);
            String Result;

            if (average.compareTo(BigDecimal.ZERO) == 0) {
                Result = "El estudiante con el codigo: " + studentCode + " no tiene calificaciones o no está inscrito en cursos.";
            } else {
                Result = "Promedio de calificaciones obtenido exitosamente para el estudiante con el Codigo: " + studentCode;
            }

            return ResponseEntity.ok(new ApiResponse<>(true, Result, average));

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error al calcular promedio del estudiante con código: {}", studentCode, e);
            return new ResponseEntity<>(new ApiResponse<>(false,
                    "Error interno al obtener el promedio: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //6.2.Opcional: Añadir un endpoint que devuelva el promedio de calificaciones de todos los
    //estudiantes en un curso específico.
    @Transactional(readOnly = true)
    @GetMapping("/GetAverageScoreByCourse/{Course_ID}")
    public ResponseEntity<ApiResponse<BigDecimal>> getAverageScoreByCourse(
            @PathVariable("Course_ID") Long Course_ID) {
        try {
            BigDecimal average = accessInscriptionService.getCourseAverageScore(Course_ID);

            String Result;
            if (average.compareTo(BigDecimal.ZERO) == 0) {
                Result = "El curso con ID " + Course_ID + " no tiene calificaciones válidas o estudiantes calificados.";
            } else {
                Result = "Promedio de calificaciones obtenido exitosamente para el curso con ID " + Course_ID;
            }

            return ResponseEntity.ok(new ApiResponse<>(true, Result, average));

        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error al calcular el promedio del curso con ID: {}", Course_ID, e);
            return new ResponseEntity<>(new ApiResponse<>(false,
                    "Error interno al obtener el promedio: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //2.1.Implementar endpoints para que los estudiantes puedan inscribirse y desinscribirse de
    //cursos.
    @PostMapping("/InscribeStudent")
    public ResponseEntity<ApiResponse<InscriptionDTO>> createInscription(
            @Validated(InscriptionOnCreate.class) @RequestBody InscriptionDTO request
    ) {
        try {
            if (request == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Solicitud Invalida", null),
                        HttpStatus.BAD_REQUEST);
            }
            InscriptionDTO created = accessInscriptionService.inscriptionStudentToCourse(request);
            return new ResponseEntity<>(new ApiResponse<>(true, "Estudiante Inscrito exitosamente", created),
                    HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Integridad de datos: " + e.getMostSpecificCause().getMessage(), null),
                    HttpStatus.CONFLICT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error createInscription", e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al Inscribir al Estudiante: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //3.1.Crear endpoints para registrar y actualizar calificaciones de los estudiantes en cada
    //curso.
    @PatchMapping("/UpdateScore/{InscriptionID}")
    public ResponseEntity<ApiResponse<InscriptionDTO>> updateScore(
            @PathVariable("InscriptionID") Long InscriptionID,
            @Validated @RequestBody UpdateScoreRequest request
    ) {
        try {
            InscriptionDTO updated = accessInscriptionService.updateScore(InscriptionID, request.getFinallyScore());
            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Calificación de la Inscripción " + InscriptionID + " actualizada exitosamente", updated));

        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error updateScore", e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al actualizar la calificación: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //2.1.Implementar endpoints para que los estudiantes puedan inscribirse y desinscribirse de
    //cursos.
    @DeleteMapping("/UnsubscribeTheCourse/{ID_Inscription}")
    public ResponseEntity<ApiResponse<Void>> unsubscribeTheCourse(
            @PathVariable("ID_Inscription") Long ID_Inscription) {
        try {
            boolean ok = accessInscriptionService.unsubscribeTheStudent(ID_Inscription);
            if (ok) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Estudiante desuscribido exitosamente", null));
            } else {
                return new ResponseEntity<>(new ApiResponse<>(false, "No se pudo desuscribir al Estudiante", null),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error unsubscribeTheCourse con el codigo: {}", ID_Inscription, e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al eliminar la inscripccion: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
