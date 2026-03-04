package com.example.fintech.service;

import com.example.fintech.dto.TransactionDTO;
import com.example.fintech.entity.Account;
import com.example.fintech.entity.Transaction;
import com.example.fintech.exception.BusinessException;
import com.example.fintech.repository.AccountRepository;
import com.example.fintech.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Transaction Service Class
 */
@Service
@RequiredArgsConstructor                    // Creates constructor for dependency injection
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Transfer money between accounts operation
     *
     * @Transactional creates a  work unity
     * If any RuntimeException occurs in here database performs a
     * Rollback returning to the previous state
     *
     * @CacheEvict tells Redis caching system that a new entry was saved
     * so our cache list must be deleted from Redis DB
     */
    @Caching(evict = {
            @CacheEvict(value = "TransactionList", allEntries = true)
            })
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

    /**
     * Get all Account trasanctions based on account Id
     *
     * @Cacheable Cache our Transaction List when retrieving it from database
     *
     * @param accountId
     * @return
     */
    @Cacheable(value = "TransactionList")
    public List<TransactionDTO> getAllTransactionsByAccount(Long accountId){
        List<Transaction> transactions =
                transactionRepository.findBySenderAccountIdOrReceiverAccountId(accountId, accountId);

        List<TransactionDTO> dtoList = new ArrayList<>();

        for(Transaction transaction : transactions) {
            dtoList.add(convertToDTO(transaction));
        }

        return dtoList;
    }



    //--------------------PRIVATE METHODS-----------------------

    /**
     * Converts our Transaction Entity in to our Transaction Data Transfer Object
     * @param transaction
     * @return
     */
    private TransactionDTO convertToDTO(Transaction transaction) {

        TransactionDTO dto = new TransactionDTO();

        dto.setId(transaction.getId());
        dto.setBalance(transaction.getBalance());
        dto.setTimestamp(transaction.getTimestamp());
        dto.setSenderId(transaction.getSenderAccount().getId());
        dto.setReceiverId(transaction.getReceiverAccount().getId());

        // Operation type
        if (transaction.getSenderAccount().getId().equals(transaction.getReceiverAccount().getId())) {
            dto.setOperationType("DEPOSIT");        // not in use
        } else {
            dto.setOperationType("TRANSFER");
        }

        return dto;
    }




}
