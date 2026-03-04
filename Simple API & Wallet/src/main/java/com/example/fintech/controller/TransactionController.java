package com.example.fintech.controller;

import com.example.fintech.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RequestMapping("/api/fintech/transaction/")
@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final static Logger logger = LogManager.getLogger(TransactionController.class);
    private final TransactionService transactionService;

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


}
