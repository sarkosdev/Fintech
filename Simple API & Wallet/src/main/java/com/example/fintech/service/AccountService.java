package com.example.fintech.service;

import com.example.fintech.entity.Account;
import com.example.fintech.entity.User;
import com.example.fintech.exception.BusinessException;
import com.example.fintech.repository.AccountRepository;
import com.example.fintech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Account Service Class
 */
@Service
@RequiredArgsConstructor                        // Creates constructor for dependency injection
public class AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    /**
     * Perform Deposit Operation in User own account
     * @param userEmail
     * @param amount
     * @return
     */
    public Account depositMoney(String userEmail, BigDecimal amount){
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException("This user dosent exist in Database"));

        Account depositAccount = accountRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new BusinessException("Account not found to perform deposit"));

        depositAccount.deposit(amount);

        return accountRepository.save(depositAccount);
    }

}
