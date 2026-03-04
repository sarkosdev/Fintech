package com.example.fintech.controller;

import com.example.fintech.service.AccountService;
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

/**
 * Account Controller class
 */
@RestController
@RequestMapping("/api/fintech/account")
@RequiredArgsConstructor                        // Creates class constructor for dependency injection
public class AccountController {

    private final static Logger logger = LogManager.getLogger(AccountController.class);
    private final AccountService accountService;

    /**
     * Deposit Money in User Account Endpoint
     * 1. Requires to be authenticated in order to perform operation
     * @param userDetails
     * @param amount
     * @return
     */
    @PostMapping("/deposit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> depositMoney(@AuthenticationPrincipal UserDetails userDetails, @RequestParam BigDecimal amount) {
        logger.info("TransactionController | METHOD: depositMoney() - DEPOSIT MONEY IN OWN ACCOUNT OPERATION");

        accountService.depositMoney(userDetails.getUsername(), amount);

        return ResponseEntity.ok("Deposit completed with success with amount: " + amount.toString());
    }



}
