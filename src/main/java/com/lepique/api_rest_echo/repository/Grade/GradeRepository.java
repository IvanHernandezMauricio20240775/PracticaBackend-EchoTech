package com.lepique.api_rest_echo.repository.Grade;

import com.lepique.api_rest_echo.domain.Grade.GradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends JpaRepository<GradeEntity, Long> {
}
