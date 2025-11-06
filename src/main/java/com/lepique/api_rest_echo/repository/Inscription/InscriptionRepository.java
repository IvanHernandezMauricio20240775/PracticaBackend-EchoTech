package com.lepique.api_rest_echo.repository.Inscription;

import com.lepique.api_rest_echo.domain.Inscription.InscriptionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscriptionRepository extends JpaRepository<InscriptionsEntity, Long> {
    List<InscriptionsEntity> findByCourse_Id(Long idCourse);

    List<InscriptionsEntity> findByStudent_Code(String studentCode);
}
