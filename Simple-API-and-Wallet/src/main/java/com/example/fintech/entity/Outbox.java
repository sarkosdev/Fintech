package com.example.fintech.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * This class maps the DB Entity 'Outbox'
 * Use case: Outbox Design Pattern
 */
@Data
@Entity
@Table( name = "outbox")
public class Outbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)                                       // Block accesses to 'id' setter
    private Long id;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "payload", nullable = false)
    private String payload;

    @Column(name = "status", nullable = false)
    private String status;

    @Setter(AccessLevel.NONE)
    @Column(name = "created_At", nullable = false)
    private LocalDateTime timestamp;



    /**
     * Automatically generates the 'transaction_date' right before inserting in DB
     */
    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }

}
