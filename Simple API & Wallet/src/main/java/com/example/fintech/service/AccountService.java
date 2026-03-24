package com.example.fintech.service;

import com.example.fintech.controller.AuthController;
import com.example.fintech.dto.WalletDTO;
import com.example.fintech.entity.Account;
import com.example.fintech.entity.User;
import com.example.fintech.exception.BusinessException;
import com.example.fintech.repository.AccountRepository;
import com.example.fintech.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Account Service Class
 */
@Service
@RequiredArgsConstructor                        // Creates constructor for dependency injection
public class AccountService {

    private static final Logger logger = LogManager.getLogger(AccountService.class);
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    /**
     * Perform Deposit Operation in User own account
     * @param userEmail
     * @param amount
     * @return Account
     */
    public Account depositMoney(String userEmail, BigDecimal amount){
        logger.info("AccountService | METHOD: depositMoney() - DEPOSIT MONEY IN TO ACCOUNT: {}", userEmail);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException("This user dosent exist in Database"));

        Account depositAccount = accountRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new BusinessException("Account not found to perform deposit"));

        depositAccount.deposit(amount);

        logger.info("AccountService | METHOD: depositMoney() - ABOUT TO SAVE DEPOSIT IN TO ACCOUNT: {}", depositAccount.getId());

        return accountRepository.save(depositAccount);
    }

    /**
     * Check Account current balance
     * @param userEmail
     * @return BigInteger
     */
    public BigDecimal currentBalance(String userEmail) {
        logger.info("AccountService | METHOD: currentBalance() - TRYING TO GET ACCOUNT CURRENT BALANCE: {}", userEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException("This user dosent exist in Database"));

        Account balanceAccount = accountRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new BusinessException("Account not found check balance"));

        logger.info("AccountService | METHOD: currentBalance() - ABOUT TO GET ACCOUNT BALANCE: {}", balanceAccount.getBalance());

        return balanceAccount.getBalance();
    }


    public List<WalletDTO> getAllAccountsByEmail(String userEmail) {
        logger.info("AccountService | METHOD: getAllAccountsByEmail() - GET ALL ACCOUNTS BY EMAIL: {}", userEmail);
        return accountRepository.findAllWallets(userEmail);
    }

}
