package com.example.fintech.service;

import com.example.fintech.entity.Account;
import com.example.fintech.entity.Transaction;
import com.example.fintech.exception.BusinessException;
import com.example.fintech.repository.AccountRepository;
import com.example.fintech.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Transaction Service Class
 */
@Service
@RequiredArgsConstructor    // Creates constructor for dependency injection
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Transfer money between accounts operation
     *
     * @Transactional creates a  work unity
     * If any RuntimeException occurs in here database performs a
     * Rollback returning to the previous state
     */
    @Transactional
    public Transaction transfer(Long senderId, Long receiverId, BigDecimal amount) {

        // Check if its a different account
        if (senderId.equals(receiverId)) {
            throw new RuntimeException("You cannot transfer money to your own account");
        }

        // Check if sender account exists in database according to account id
        Account sender = accountRepository.findById(senderId)
                .orElseThrow(() -> new BusinessException("Sender account not found"));

        // Check if receiver account exists in database according to account id
        Account receiver = accountRepository.findById(receiverId)
                .orElseThrow(() -> new BusinessException("Receiver account not found"));


        // Business Logic
        // 1. withdraw from sender account
        sender.withdraw(amount);
        // 2. deposit in to receiver account
        receiver.deposit(amount);

        // Save both changes in database
        accountRepository.save(sender);
        accountRepository.save(receiver);

        // Saves transaction operation in database
        Transaction tx = new Transaction();
        tx.setSenderAccount(sender);
        tx.setReceiverAccount(receiver);
        tx.setBalance(amount);

        return transactionRepository.save(tx);
    }


}
