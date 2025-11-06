package com.lepique.api_rest_echo.controller;

import com.lepique.api_rest_echo.domain.Greeting;
import com.lepique.api_rest_echo.service.GreetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Greeting entities.
 * Provides endpoints for creating, retrieving, updating, deleting, and searching greetings.
 */
@RestController
@RequestMapping("/api/greetings")
public class GreetingController {

    private final GreetingService greetingService;
        /**
     *      * Constructs a new GreetingController with the specified GreetingService.
     *      *
     *      * @param greetingService the service to be used for managing Greeting entities
     */
    @Autowired
    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    /**
     * Creates a new Greeting entity.
     *
     * @param greeting the Greeting entity to be created
     * @return a ResponseEntity containing the created Greeting entity and HTTP status CREATED
     */
    @PostMapping
    public ResponseEntity<Greeting> createGreeting(@RequestBody Greeting greeting) {
        Greeting createdGreeting = greetingService.createGreeting(greeting);
        return new ResponseEntity<>(createdGreeting, HttpStatus.CREATED);
    }

    /**
     * Retrieves a Greeting entity by its ID.
     *
     * @param id the ID of the Greeting entity to be retrieved
     * @return a ResponseEntity containing the retrieved Greeting entity and HTTP status OK, or HTTP status NOT FOUND if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Greeting> getGreetingById(@PathVariable Long id) {
        Optional<Greeting> greeting = greetingService.getGreetingById(id);
        return greeting.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(null));
    }

    /**
     * Retrieves all Greeting entities.
     *
     * @return a ResponseEntity containing a list of all Greeting entities and HTTP status OK
     */
    @GetMapping
    public ResponseEntity<List<Greeting>> getAllGreetings() {
        List<Greeting> greetings = greetingService.getAllGreetings();
        return new ResponseEntity<>(greetings, HttpStatus.OK);
    }

    /**
     * Updates an existing Greeting entity.
     *
     * @param id the ID of the Greeting entity to be updated
     * @param updatedGreeting the updated Greeting entity
     * @return a ResponseEntity containing the updated Greeting entity and HTTP status OK, or HTTP status NOT FOUND if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Greeting> updateGreeting(@PathVariable Long id, @RequestBody Greeting updatedGreeting) {
        try {
            Greeting greeting = greetingService.updateGreeting(id, updatedGreeting);
            return new ResponseEntity<>(greeting, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Deletes a Greeting entity by its ID.
     *
     * @param id the ID of the Greeting entity to be deleted
     * @return a ResponseEntity with HTTP status NO CONTENT if deleted, or HTTP status NOT FOUND if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGreeting(@PathVariable Long id) {
        try {
            greetingService.deleteGreeting(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Searches for Greeting entities by sender.
     *
     * @param sender the sender to search for
     * @return a ResponseEntity containing a list of Greeting entities with the specified sender and HTTP status OK
     */
    @GetMapping("/search")
    public ResponseEntity<List<Greeting>> searchGreetingsBySender(@RequestParam String sender) {
        List<Greeting> greetings = greetingService.searchGreetingsBySender(sender);
        return new ResponseEntity<>(greetings, HttpStatus.OK);
    }
}