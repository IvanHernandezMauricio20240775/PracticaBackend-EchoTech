package com.lepique.api_rest_echo.service;

import com.lepique.api_rest_echo.domain.Greeting;
import com.lepique.api_rest_echo.repository.GreetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing Greeting entities.
 * Provides methods for creating, retrieving, updating, deleting, and searching greetings.
 */
@Service
public class GreetingService {
    private final GreetingRepository greetingRepository;

    /**
     * Constructs a new GreetingService with the specified GreetingRepository.
     *
     * @param greetingRepository the repository to be used for accessing Greeting entities
     */
    @Autowired
    public GreetingService(GreetingRepository greetingRepository) {
        this.greetingRepository = greetingRepository;
    }

    /**
     * Creates a new Greeting entity.
     *
     * @param greeting the Greeting entity to be created
     * @return the created Greeting entity
     */
    public Greeting createGreeting(Greeting greeting) {
        return greetingRepository.save(greeting);
    }

    /**
     * Retrieves a Greeting entity by its ID.
     *
     * @param id the ID of the Greeting entity to be retrieved
     * @return an Optional containing the retrieved Greeting entity, or an empty Optional if not found
     */
    public Optional<Greeting> getGreetingById(Long id) {
        return greetingRepository.findById(id);
    }

    /**
     * Retrieves all Greeting entities.
     *
     * @return a list of all Greeting entities
     */
    public List<Greeting> getAllGreetings() {
        return greetingRepository.findAll();
    }

    /**
     * Updates an existing Greeting entity.
     *
     * @param id the ID of the Greeting entity to be updated
     * @param updatedGreeting the updated Greeting entity
     * @return the updated Greeting entity
     * @throws RuntimeException if the Greeting entity with the specified ID is not found
     */
    public Greeting updateGreeting(Long id, Greeting updatedGreeting) {
        return greetingRepository.findById(id)
                .map(greeting -> {
                    greeting.setName(updatedGreeting.getName());
                    greeting.setSender(updatedGreeting.getSender());
                    greeting.setRecipient(updatedGreeting.getRecipient());
                    return greetingRepository.save(greeting);
                })
                .orElseThrow(() -> new RuntimeException("Greeting not found with id: " + id));
    }

    /**
     * Deletes a Greeting entity by its ID.
     *
     * @param id the ID of the Greeting entity to be deleted
     * @throws RuntimeException if the Greeting entity with the specified ID is not found
     */
    public void deleteGreeting(Long id) {
        if (greetingRepository.existsById(id)) {
            greetingRepository.deleteById(id);
        } else {
            throw new RuntimeException("Greeting not found with id: " + id);
        }
    }

    /**
     * Searches for Greeting entities by sender.
     *
     * @param sender the sender to search for
     * @return a list of Greeting entities with the specified sender
     */
    public List<Greeting> searchGreetingsBySender(String sender) {
        return greetingRepository.findAll()
                .stream()
                .filter(greeting -> greeting.getSender().equalsIgnoreCase(sender))
                .toList();
    }
}