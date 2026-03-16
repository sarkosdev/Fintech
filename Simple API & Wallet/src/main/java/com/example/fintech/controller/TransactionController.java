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
import java.util.Map;

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
     * @param receiverId
     * @param amount
     * @return ResponseEntity<String>
     */
    @PostMapping("/withdraw")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> sendMoneyToAccount(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long receiverId,
            @RequestParam BigDecimal amount
            ){
        logger.info("TransactionController | METHOD: sendMoneyToAccount() - SEND MONEY FROM USER ACCOUNT {} TO USER ACCOUNT {}", userDetails.getUsername(), receiverId);
        transactionService.transfer(userDetails.getUsername(), receiverId, amount);
        return ResponseEntity.ok(Map.of("msg", "Transaction completed with success"));
    }

    /**
     * Get Transaction History by User email
     * @param userDetails
     * @return List<TransactionDTO>
     */
    @GetMapping("/wallet-transactions-history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TransactionDTO>> getAllTransactions(
            @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("TransactionController | METHOD: getAllTransactions() - GET ALL USER ACCOUNT TRANSACTIONS {}", userDetails.getUsername());
        return ResponseEntity.ok(transactionService.findAllTransactionsByUserEmail(userDetails.getUsername()));
    }

}
