package com.example.fintech.repository;

import com.example.fintech.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Outbox Repository Interface
 */
public interface OutboxRepository extends JpaRepository<Outbox, Long> {

    // Querys only for the top 20 records in order to improve performance
    List<Outbox> findTop20ByStatusOrderByTimestampAsc(String status);
}
