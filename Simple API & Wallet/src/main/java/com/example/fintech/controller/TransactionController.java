package com.example.fintech.controller;

import com.example.fintech.dto.TransactionDTO;
import com.example.fintech.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Transaction Controller class
 */
@RequestMapping("/api/fintech/transaction/")
@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final static Logger logger = LogManager.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    /**
     * Send Money from one account to another by Account id
     * @param userDetails
     * @param senderId
     * @param receiverId
     * @param amount
     * @return ResponseEntity<String>
     */
    @PostMapping("/withdraw")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> sendMoneyToAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam BigDecimal amount
            ){
        logger.info("TransactionController | METHOD: sendMoneyToAccount() - SEND MONEY FROM USER ACCOUNT {} TO USER ACCOUNT {}", senderId, receiverId);
        transactionService.transfer(senderId, receiverId, amount);
        return ResponseEntity.ok("Transaction completed with success");
    }


    @GetMapping("/getAllTransactions/{accountId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TransactionDTO>> getAllTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long accountId) {
        logger.info("TransactionController | METHOD: getAllTransactions() - GET ALL USER ACCOUNT TRANSACTIONS {}", accountId);
        return ResponseEntity.ok(transactionService.getAllTransactionsByAccount(accountId));
    }



}
