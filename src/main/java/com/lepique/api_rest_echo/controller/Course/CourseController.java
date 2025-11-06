package com.lepique.api_rest_echo.controller.Course;

import com.lepique.api_rest_echo.domain.Course.CourseEntity;
import com.lepique.api_rest_echo.models.DTO.Course.CourseDTO;
import com.lepique.api_rest_echo.models.DTO.Teacher.TeacherDTO;
import com.lepique.api_rest_echo.models.apiResponse.ApiResponse;
import com.lepique.api_rest_echo.repository.Teacher.TeacherOnCreate;
import com.lepique.api_rest_echo.service.Course.CourseService;
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
@RequestMapping("/ActionsCourse")
@Slf4j
public class CourseController {

    @Autowired
    private CourseService accessCourseService;

    @Transactional(readOnly = true)
    @GetMapping("/GetAllCourse")
    public ResponseEntity<ApiResponse<List<CourseDTO>>> getAllCourse() {
        try {
            List<CourseDTO> list = accessCourseService.getAllCourses();
            return ResponseEntity.ok(new ApiResponse<>(true, "Cursos Obtenidos Exitosamente", list));
        } catch (Exception e) {
            log.error("Error getAllCourse", e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al obtener los Cursos: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    @GetMapping("/GetCourseByID/{ID_Course}")
    public ResponseEntity<ApiResponse<CourseDTO>> getTeacherByCode(
            @PathVariable("ID_Course") Long ID_Course) {
        try {
            CourseDTO course = accessCourseService.getCourseByID(ID_Course);
            return ResponseEntity.ok(new ApiResponse<>(true, "Curso Obtenido correctamente", course));
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error al obtener el Curso por ID: {}", ID_Course, e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al obtener el Curso: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //4.2.Implementar un endpoint para consultar todos los cursos impartidos por un profesor
    //específico.
    @Transactional(readOnly = true)
    @GetMapping("/GetCourseByTeacherCode/{TeacherCode}")
    public ResponseEntity<ApiResponse<List<CourseDTO>>> getAllCourseByTeacherCode(
            @PathVariable("TeacherCode") String TeacherCode) {
        try {
            List<CourseDTO> list = accessCourseService.getCoursesByTeacher(TeacherCode);
            return ResponseEntity.ok(new ApiResponse<>(true, "Cursos Impartidos por el profesor con el Codigo:" + TeacherCode + " han sido Obtenidos Exitosamente", list));
        } catch (Exception e) {
            log.error("Error getAllCourseByTeacherCode", e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al obtener los Cursos impartidos por el profesor con el codigo: " + TeacherCode + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/CreateCourse")
    public ResponseEntity<ApiResponse<CourseDTO>> createCourse(
            @Valid @RequestBody CourseDTO request
    ) {
        try {
            if (request == null) {
                return new ResponseEntity<>(new ApiResponse<>(false, "Solicitud Invalida", null),
                        HttpStatus.BAD_REQUEST);
            }
            CourseDTO created = accessCourseService.createCourse(request);
            return new ResponseEntity<>(new ApiResponse<>(true, "Curso creado exitosamente", created),
                    HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Integridad de datos: " + e.getMostSpecificCause().getMessage(), null),
                    HttpStatus.CONFLICT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error createCourse", e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al crear el Curso: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/UpdateCourse/{ID_Course}")
    public ResponseEntity<ApiResponse<CourseDTO>> updateCourse(
            @PathVariable("ID_Course") Long ID_Course,
            @Valid @RequestBody CourseDTO dto
    ) {
        try {
            CourseDTO updated = accessCourseService.updateCourse(ID_Course, dto);
            return ResponseEntity.ok(new ApiResponse<>(true, "Curso actualizado exitosamente", updated));

        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, "Integridad de datos: " + e.getMostSpecificCause().getMessage(), null),
                    HttpStatus.CONFLICT);
        } catch (Exception e) {
            log.error("Error updateCourse con el ID: {}", ID_Course, e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al actualizar el Curso: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/DeleteCourse/{ID_Course}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(
            @PathVariable("ID_Course") Long ID_Course) {
        try {
            boolean ok = accessCourseService.deleteCourse(ID_Course);
            if (ok) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Curso eliminado exitosamente", null));
            } else {
                return new ResponseEntity<>(new ApiResponse<>(false, "No se pudo eliminar el Curso", null),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(new ApiResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error deleteCourse con el codigo: {}", ID_Course, e);
            return new ResponseEntity<>(new ApiResponse<>(false, "Error al eliminar el Curso: " + e.getMessage(), null),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
