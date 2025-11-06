package com.lepique.api_rest_echo.controller.Teacher;

import com.lepique.api_rest_echo.models.DTO.CourseTeacher.CourseTeacherDTO;
import com.lepique.api_rest_echo.models.DTO.Students.StudentsDTO;
import com.lepique.api_rest_echo.models.DTO.Teacher.CourseIDDeleteRequest;
import com.lepique.api_rest_echo.models.DTO.Teacher.TeacherDTO;
import com.lepique.api_rest_echo.models.apiResponse.ApiResponse;
import com.lepique.api_rest_echo.repository.Students.StudentsOnCreate;
import com.lepique.api_rest_echo.repository.Teacher.TeacherOnCreate;
import com.lepique.api_rest_echo.service.Teacher.TeacherService;
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
@RequestMapping("/ActionsTeacher")
@Slf4j
public class TeacherController {

    @Autowired
    private TeacherService accessTeacherService;

    @Transactional(readOnly = true)
    @GetMapping("/GetAllTeachers")
    public ResponseEntity<ApiResponse<List<TeacherDTO>>> getAllTeachers() {
        try {
            List<TeacherDTO> list = accessTeacherService.getAllTeacher();
            return ResponseEntity.ok(new ApiResponse<>(true, "Profesores Obtenidos Exitosamente", list));
        } catch (Exception e) {
            log.error("Error getAllTeachers", e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al obtener los Profesores: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    @GetMapping("/GetTeacherByCode/{Code}")
    public ResponseEntity<ApiResponse<TeacherDTO>> getTeacherByCode(
            @PathVariable("Code") String Code) {
        try {
            TeacherDTO teacher = accessTeacherService.getTeacherByCode(Code);
            return ResponseEntity.ok(new ApiResponse<>(true, "Docente Obtenido correctamente", teacher));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error al obtener el Docente por Codigo: {}", Code, e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al obtener el Docente: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/CreateTeacher")
    public ResponseEntity<ApiResponse<TeacherDTO>> createTeacher(
            @Validated(TeacherOnCreate.class) @RequestBody TeacherDTO request
    ) {
        try {
            if (request == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Solicitud Invalida", null),
                        HttpStatus.BAD_REQUEST);
            }
            TeacherDTO created = accessTeacherService.createNewTeacher(request);
            return new ResponseEntity<>(new ApiResponse<>(true, "Docente creado exitosamente", created),
                    HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Integridad de datos: " + e.getMostSpecificCause().getMessage(), null),
                    HttpStatus.CONFLICT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error createTeacher", e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al crear el Docente: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /*
     4.1.Crear un endpoint para asignar un profesor a un curso.
    */
    @PostMapping("/AssignTeacherToACourse")
    public ResponseEntity<ApiResponse<TeacherDTO>> assignTeacherToACourse(
            @Valid @RequestBody CourseTeacherDTO request
    ) {
        try {
            if (request == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Solicitud Invalida", null),
                        HttpStatus.BAD_REQUEST);
            }
            TeacherDTO created = accessTeacherService.addTeacherToCourse(request);
            return new ResponseEntity<>(new ApiResponse<>(true, "El Docente ha sido  asignado al Curso exitosamente", created),
                    HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Integridad de datos: " + e.getMostSpecificCause().getMessage(), null),
                    HttpStatus.CONFLICT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error createTeacher", e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al Asignar al Docente en el Curso: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/UpdateTeacher/{Code}")
    public ResponseEntity<ApiResponse<TeacherDTO>> updateTeacher(
            @PathVariable("Code") String Code,
            @Valid @RequestBody TeacherDTO dto
    ) {
        try {
            TeacherDTO updated = accessTeacherService.updateTeacher(Code, dto);
            return ResponseEntity.ok(new ApiResponse<>(true, "Docente actualizado exitosamente", updated));

        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Integridad de datos: " + e.getMostSpecificCause().getMessage(), null),
                    HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Error updateTeacher con el codigo: {}", Code, e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al actualizar el Docente: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/DeleteTeacher/{Code}")
    public ResponseEntity<ApiResponse<Void>> deleteTeacher(
            @PathVariable("Code") String Code) {
        try {
            boolean ok = accessTeacherService.deleteTeacher(Code);
            if (ok) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Docente eliminado exitosamente", null));
            } else {
                return new ResponseEntity<>(new ApiResponse<>(false, "No se pudo eliminar al Docente", null),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error deleteTeacher con el codigo: {}", Code, e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al eliminar al Docente: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/DeleteTeacherFromCourse/{TeacherCode}")
    public ResponseEntity<ApiResponse<Void>> deleteTeacherFromCourse(
            @PathVariable("TeacherCode") String teacherCode,
            @Validated @RequestBody CourseIDDeleteRequest request) {
        try {

            accessTeacherService.deleteTeacherAssignmentByCodes(teacherCode, request.getIdCourse());

            return ResponseEntity.ok(new ApiResponse<>(true,
                    "Docente " + teacherCode + " removido del curso " + request.getIdCourse() + " exitosamente.", null));

        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error al eliminar asignación. Docente: {}, Curso ID: {}", teacherCode, request.getIdCourse(), e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al eliminar la asignación: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
