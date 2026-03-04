package com.example.fintech.repository;

import com.example.fintech.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Transaction Repository Interface
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Query for all Transactions in DB according to DB
     * @param senderId
     * @param receiverId
     * @return
     */
    List<Transaction> findBySenderAccountIdOrReceiverAccountId(Long senderId, Long receiverId);




}
