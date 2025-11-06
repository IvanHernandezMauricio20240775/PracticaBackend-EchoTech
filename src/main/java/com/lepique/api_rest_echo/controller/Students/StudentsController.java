package com.lepique.api_rest_echo.controller.Students;

import com.lepique.api_rest_echo.models.DTO.Students.StudentsDTO;
import com.lepique.api_rest_echo.models.apiResponse.ApiResponse;
import com.lepique.api_rest_echo.repository.Students.StudentsOnCreate;
import com.lepique.api_rest_echo.service.Students.StudentsService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/StudentsActions")
@Slf4j
public class StudentsController {

    //1. Operaciones CRUD para Estudiantes:
    @Autowired
    private StudentsService accessStudentService;

    //1.1.Crear endpoints que permitan crear, leer, actualizar y eliminar estudiantes.
    //1.2.Cada estudiante debe incluir información sobre su nombre, edad y grado al que
    //pertenece.
    @Transactional(readOnly = true)
    @GetMapping("/GetAllStudents")
    public ResponseEntity<ApiResponse<List<StudentsDTO>>> getAllStudents() {
        try {
            List<StudentsDTO> list = accessStudentService.GetAllStudents();
            return ResponseEntity.ok(new ApiResponse<>(true, "Estudiantes Obtenidos Exitosamente", list));
        } catch (Exception e) {
            log.error("Error getAllStudents", e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al obtener los Estudiantes: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    @GetMapping("/GetStudentByCode/{Code}")
    public ResponseEntity<ApiResponse<StudentsDTO>> getStudentByCode(
            @PathVariable("Code") String Code) {
        try {
            StudentsDTO student = accessStudentService.getStudentByCode(Code);
            return ResponseEntity.ok(new ApiResponse<>(true, "Estudiante Obtenido correctamente", student));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error al obtener el Estudiante por Codigo: {}", Code, e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al obtener el Estudiante: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
    5.1.Crear un endpoint para listar estudiantes por grado.
    */
    @Transactional(readOnly = true)
    @GetMapping("/GetStudentsByGrade/{ID_Grade}")
    public ResponseEntity<ApiResponse<List<StudentsDTO>>> getStudentsByGrade(
            @PathVariable("ID_Grade") Long ID_Grade
    ) {
        try {
            List<StudentsDTO> list = accessStudentService.GetStudentsByGradeId(ID_Grade);
            return ResponseEntity.ok(new ApiResponse<>(true, "Estudiantes Obtenidos por su Grado Exitosamente", list));
        } catch (Exception e) {
            log.error("Error getStudentsByGrade", e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al obtener los Estudiantes por Grado: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/CreateStudent")
    public ResponseEntity<ApiResponse<StudentsDTO>> createStudent(
            @Validated(StudentsOnCreate.class) @RequestBody StudentsDTO request
    ) {
        try {
            if (request == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Solicitud Invalida", null),
                        HttpStatus.BAD_REQUEST);
            }
            StudentsDTO created = accessStudentService.CreateNewStudent(request);
            return new ResponseEntity<>(new ApiResponse<>(true, "Estudiante creado exitosamente", created),
                    HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Integridad de datos: " + e.getMostSpecificCause().getMessage(), null),
                    HttpStatus.CONFLICT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error createStudent", e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al crear el Estudiante: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/UpdateStudent/{Code}")
    public ResponseEntity<ApiResponse<StudentsDTO>> updateStudent(
            @PathVariable("Code") String Code,
            @Valid @RequestBody StudentsDTO dto
    ) {
        try {
            StudentsDTO updated = accessStudentService.UpdateStudent(Code, dto);
            return ResponseEntity.ok(new ApiResponse<>(true, "Estudiante actualizado exitosamente", updated));

        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Integridad de datos: " + e.getMostSpecificCause().getMessage(), null),
                    HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Error updateStudent con el codigo: {}", Code, e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al actualizar el Estudiante: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/DeleteStudent/{Code}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(
            @PathVariable("Code") String Code) {
        try {
            boolean ok = accessStudentService.deleteStudent(Code);
            if (ok) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Estudiante eliminado exitosamente", null));
            } else {
                return new ResponseEntity<>(new ApiResponse<>(false, "No se pudo eliminar el Estudiante", null),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error deleteStudent con el codigo: {}", Code, e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al eliminar el Estudiante: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
