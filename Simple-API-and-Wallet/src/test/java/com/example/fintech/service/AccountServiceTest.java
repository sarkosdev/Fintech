package com.example.fintech.service;

import com.example.fintech.entity.Account;
import com.example.fintech.entity.User;
import com.example.fintech.exception.BusinessException;
import com.example.fintech.helper.TestUserFactory;
import com.example.fintech.repository.AccountRepository;
import com.example.fintech.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Account Service Test Class
 */
@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;


    /**
     * Test Account Deposit Money Successfully
     */
    @Test
    void AccountService_DepositMoneySuccessfully_shouldAccount() {

        // Arrange - Mock User
        // Used to test user setter Id
        User user = TestUserFactory.userWithId(1L);
        user.setEmail("lily@lily.com");

        Account account = new Account();
        account.setUser(user);
        account.setBalance(new BigDecimal(0));

        // Test email already exists functionality
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        // Test password encoder functionality
        when(accountRepository.findByUser_Id(anyLong()))
                .thenReturn(Optional.of(account));

        // Mock .save() method and return the Account supposed to be saved
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        Account result = accountService.depositMoney(user.getEmail(), new BigDecimal(100));


        // ASSERT
        assertNotNull(result);
        assertEquals(new BigDecimal(100), result.getBalance());
        assertNotEquals(new BigDecimal(0), result.getBalance());

        // Check number of invocations per class
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    /**
     * Test Account Deposit Money Business Exception - This user dosent exist in Database
     */
    @Test
    void AccountService_DepositMoney_shouldReturnBusinessException_whenUserDontExist() {

        // Arrange
        User user = new User();
        user.setEmail("lily@lily.com");

        // Act
        // Checks for the Email - it must return a new user
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.empty());

        // Throws the Business Exception
        BusinessException exception = assertThrows(BusinessException.class, () -> {
           accountService.depositMoney(user.getEmail(), new BigDecimal(100));
        });

        // Assert
        assertEquals("This user dosent exist in Database", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    /**
     * Test Account Deposit Money Business Exception - Account not found to deposit
     */
    @Test
    void AccountService_DepositMoney_shouldReturnBusinessException_whenAccountNotFound() {

        // Arrange
        User user = new User();
        user.setEmail("lily@lily.com");

        // Act
        // Checks for the Email - it must return a new user
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        // Throws the Business Exception
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            accountService.depositMoney(user.getEmail(), new BigDecimal(100));
        });

        // Assert
        assertEquals("Account not found to perform deposit", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    /**
     * Test Account Current Balance
     */
    @Test
    void AccountService_CurrentBalance_shouldReturnAccountBalance() {

        // Arrange
        User user = TestUserFactory.userWithId(1L);
        user.setEmail("lily@lily.com");

        Account account = new Account();
        account.setUser(user);
        account.setBalance(new BigDecimal(500));

        // Act
        // Checks for email - should return same user
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        // Checks for account by User id - should return same account
        when(accountRepository.findByUser_Id(anyLong()))
                .thenReturn(Optional.of(account));

        BigDecimal balance = accountService.currentBalance(user.getEmail());


        // Assert
        assertNotNull(balance);
        assertEquals(balance, account.getBalance());
    }


    /**
     * Test Account Current Balance Business Exception - This user dosent exist in Database
     */
    @Test
    void AccountService_CurrentBalance_shouldReturnBusinessException_whenUserDosentExist() {

        // Arrange
        User user = new User();
        user.setEmail("lily@lily.com");

        // Act
        // Checks for the Email - it must return a new user
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.empty());

        // Throws the Business Exception
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            accountService.currentBalance(user.getEmail());
        });

        // Assert
        assertEquals("This user dosent exist in Database", exception.getMessage());

        verify(userRepository, never()).save(any());
    }


    /**
     * Test Account Current Balance Business Exception - Account not found in DB
     */
    @Test
    void AccountService_CurrentBalance_shouldReturnBusinessException_whenAccountNotFound() {

        // Arrange
        User user = TestUserFactory.userWithId(1L);
        user.setEmail("lily@lily.com");

        // Act
        // Checks for the Email - it must return a new user
        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        // Checks for Account by User Id - it must come empty
        when(accountRepository.findByUser_Id(user.getId()))
                .thenReturn(Optional.empty());

        // Throws the Business Exception
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            accountService.currentBalance(user.getEmail());
        });

        // Assert
        assertEquals("Account not found check balance", exception.getMessage());

        verify(userRepository, never()).save(any());
    }




}
