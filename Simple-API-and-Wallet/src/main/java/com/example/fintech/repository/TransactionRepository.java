package com.example.fintech.repository;

import com.example.fintech.dto.TransactionDTO;
import com.example.fintech.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * Query for all Transactions in DB according to User email
     * Also this query will differ the deposits and withdrawls
     * @param email
     * @return
     */
    @Query("SELECT new com.example.fintech.dto.TransactionDTO(\n" +
            "            t.id,\n" +
            "            CASE\n" +
            "                WHEN su.email = :email THEN -t.balance\n" +
            "                ELSE t.balance\n" +
            "            END,\n" +
            "            t.timestamp,\n" +
            "            sa.id,\n" +
            "            ra.id,\n" +
            "            CASE\n" +
            "                WHEN su.email = :email THEN 'PAYMENT'\n" +
            "                ELSE 'CREDIT'\n" +
            "            END,\n" +
            "            CASE\n" +
            "                WHEN su.email = :email THEN ru.email\n" +
            "                ELSE su.email\n" +
            "            END\n" +
            "       )\n" +
            "       FROM Transaction t\n" +
            "       JOIN t.senderAccount sa\n" +
            "       JOIN sa.user su\n" +
            "       JOIN t.receiverAccount ra\n" +
            "       JOIN ra.user ru\n" +
            "       WHERE su.email = :email\n" +
            "          OR ru.email = :email\n" +
            "       ORDER BY t.timestamp DESC")
    List<TransactionDTO> findAllTransactionsByUserEmail(@Param("email") String email);
}
