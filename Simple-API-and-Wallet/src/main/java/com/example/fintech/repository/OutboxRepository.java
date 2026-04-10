package com.example.fintech.repository;

import com.example.fintech.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {

    List<Outbox> findAllByStatus(String status);
}
