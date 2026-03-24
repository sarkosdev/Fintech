package com.example.fintech.service;

import com.example.fintech.dto.TransactionDTO;
import com.example.fintech.entity.Account;
import com.example.fintech.entity.Transaction;
import com.example.fintech.exception.BusinessException;
import com.example.fintech.repository.AccountRepository;
import com.example.fintech.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger(TransactionService.class);
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
            @CacheEvict(value = "TransactionList", allEntries = true),
            @CacheEvict(value = "AccountTransactions", allEntries = true)
            })
    @Transactional
    public Transaction transfer(String userEmail, Long receiverId, BigDecimal amount) {
        logger.info("TransactionService | METHOD: transfer() - TRYING TO TRANSFER MONEY BETWEEN ACCOUNTS " +
                "senderEmail: {}, " +
                "receiverId: {}, " +
                "amount: {}, ",
                userEmail,
                receiverId,
                amount);


        // Check if sender account exists in database according to account id
        Account senderAccount = accountRepository.findByUser_Email(userEmail)
                .orElseThrow(() -> new BusinessException("Sender account not found"));

        // Check if its a different account
        if (senderAccount.getId().equals(receiverId)) {
            throw new BusinessException("You cannot transfer money to your own account");
        }

        // Check if receiver account exists in database according to account id
        Account receiver = accountRepository.findById(receiverId)
                .orElseThrow(() -> new BusinessException("Receiver account not found"));

        // Business Logic
        // 1. withdraw from sender account
        senderAccount.withdraw(amount);
        // 2. deposit in to receiver account
        receiver.deposit(amount);

        // Save both changes in database
        accountRepository.save(senderAccount);
        accountRepository.save(receiver);

        // Saves transaction operation in database
        Transaction tx = new Transaction();
        tx.setSenderAccount(senderAccount);
        tx.setReceiverAccount(receiver);
        tx.setBalance(amount);

        logger.info("TransactionService | METHOD: transfer() - ABOUT TO SAVE TRANSACTION IN Account {}, with Amount {}",
                tx.getReceiverAccount().getId(),
                amount);

        return transactionRepository.save(tx);
    }



    /**
     * Get all Account trasanctions based on account Id
     *
     * @Cacheable Cache our Transaction List when retrieving it from database
     *
     * @param
     * @return
     */
     /* DEPRECATED
    @Cacheable(value = "TransactionList")
    public List<TransactionDTO> getAllTransactionsByAccount(String userEmail){
        List<Transaction> transactions =
                transactionRepository.findBySenderAccountIdOrReceiverAccountId(null, null);

        List<TransactionDTO> dtoList = new ArrayList<>();

        for(Transaction transaction : transactions) {
            dtoList.add(convertToDTO(transaction));
        }

        return dtoList;
    }
    */

    @Cacheable(value = "AccountTransactions")
    public List<TransactionDTO> findAllTransactionsByUserEmail(String email) {
        List<TransactionDTO> transactions = transactionRepository.findAllTransactionsByUserEmail(email);

        if(transactions == null) {
            return new ArrayList<TransactionDTO>();
        }

        return transactions;
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
