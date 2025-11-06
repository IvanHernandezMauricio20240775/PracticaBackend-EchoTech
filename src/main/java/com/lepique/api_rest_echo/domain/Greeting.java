package com.lepique.api_rest_echo.domain;

import jakarta.persistence.*;

/**
 * Entity class representing a Greeting.
 */
@Entity
@Table(name = "GREETINGS")
public class Greeting {

    /**
     * The unique identifier for the Greeting.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Maps to Oracle's GENERATED AS IDENTITY
    private Long id;

    /**
     * The name of the Greeting.
     */
    private String name;

    /**
     * The sender of the Greeting.
     */
    private String sender;

    /**
     * The recipient of the Greeting.
     */
    private String recipient;

    /**
     * Default constructor.
     */
    public Greeting() {
    }

    /**
     * Parameterized constructor.
     *
     * @param name      the name of the Greeting
     * @param sender    the sender of the Greeting
     * @param recipient the recipient of the Greeting
     */
    public Greeting(String name, String sender, String recipient) {
        this.name = name;
        this.sender = sender;
        this.recipient = recipient;
    }

    /**
     * Gets the unique identifier for the Greeting.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the Greeting.
     *
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the Greeting.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the Greeting.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the sender of the Greeting.
     *
     * @return the sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * Sets the sender of the Greeting.
     *
     * @param sender the sender to set
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Gets the recipient of the Greeting.
     *
     * @return the recipient
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Sets the recipient of the Greeting.
     *
     * @param recipient the recipient to set
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    /**
     * Returns a string representation of the Greeting.
     *
     * @return a string representation of the Greeting
     */
    @Override
    public String toString() {
        return "Greeting{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                '}';
    }
}