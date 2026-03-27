package com.example.fintech.controller;

import com.example.fintech.dto.ApiResponseWrapper;
import com.example.fintech.dto.WalletDTO;
import com.example.fintech.service.AccountService;
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
     * @return ResponseEntity<ApiResponseWrapper>
     */
    @PostMapping("/deposit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper> depositMoney(@AuthenticationPrincipal UserDetails userDetails, @RequestParam BigDecimal amount) {
        logger.info("TransactionController | METHOD: depositMoney() - DEPOSIT MONEY IN OWN ACCOUNT OPERATION");

        accountService.depositMoney(userDetails.getUsername(), amount);

        return ResponseEntity.ok(
                ApiResponseWrapper.success("Deposit completed with success")
        );
    }

    /**
     * Get Account current balance for UI porpouse
     * @param userDetails
     * @return ResponseEntity<Map<String, BigDecimal>>
     */
    @GetMapping("/balance")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, BigDecimal>> currentBalance(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("TransactionController | METHOD: currentBalance() - GET CURRENT WALLET BALANCE");

        return ResponseEntity.ok(Map.of("balance", accountService.currentBalance(userDetails.getUsername())));

    }

    /**
     * Get all Accounts available on the system
     * @param userDetails
     * @return ResponseEntity<List<WalletDTO>>
     */
    @GetMapping("/get-all-accounts-system")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WalletDTO>> getAllAccountsByEmail(@AuthenticationPrincipal UserDetails userDetails){
        logger.info("TransactionController | METHOD: getAllAccountsByEmail() - GET ALL WALLETS AVAILABLE TO TRANSFER MONEY");
        return ResponseEntity.ok(accountService.getAllAccountsByEmail(userDetails.getUsername()));
    }



}
