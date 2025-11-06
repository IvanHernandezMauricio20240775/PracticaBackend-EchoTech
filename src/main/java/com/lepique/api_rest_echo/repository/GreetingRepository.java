package com.lepique.api_rest_echo.repository;

import com.lepique.api_rest_echo.domain.Greeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing Greeting entities.
 * Extends JpaRepository to provide CRUD operations and custom queries.
 */
@Repository
public interface GreetingRepository extends JpaRepository<Greeting, Long> {
    // Custom queries (if needed) can be added here
}