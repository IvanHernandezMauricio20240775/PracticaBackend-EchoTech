package com.lepique.api_rest_echo.repository.Course;

import com.lepique.api_rest_echo.domain.Course.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {
}
