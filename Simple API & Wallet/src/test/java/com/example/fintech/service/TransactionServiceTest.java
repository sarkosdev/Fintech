package com.example.fintech.service;

import com.example.fintech.entity.Account;
import com.example.fintech.entity.Transaction;
import com.example.fintech.entity.User;
import com.example.fintech.exception.BusinessException;
import com.example.fintech.helper.TestAccountFactory;
import com.example.fintech.helper.TestUserFactory;
import com.example.fintech.repository.AccountRepository;
import com.example.fintech.repository.TransactionRepository;
import com.example.fintech.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Transaction Service Test Class
 */
@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    /**
     * Test transfer money successfully from one account to another
     */
    @Test
    void TransactionService_TransferSuccessfully_shouldReturnTransaction() {

        // ARRANGE
        User senderUser = TestUserFactory.userWithId(1L);
        senderUser.setEmail("sender@sender.com");

        User receiverUser = TestUserFactory.userWithId(2L);
        senderUser.setEmail("receiver@receiver.com");

        Account sender = TestAccountFactory.accountWithId(1L);
        sender.setUser(senderUser);

        Account receiver = TestAccountFactory.accountWithId(2L);
        receiver.setUser(receiverUser);

        // ACT
        when(accountRepository.findByUser_Email(senderUser.getEmail()))
                .thenReturn(Optional.of(sender));

        when(accountRepository.findById(receiver.getId()))
                .thenReturn(Optional.of(receiver));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction transaction = transactionService.transfer(
                senderUser.getEmail(),
                receiver.getId(),
                new BigDecimal(100));

        // ASSERT
        assertNotNull(transaction);
        assertEquals(transaction.getReceiverAccount().getId(), receiver.getId());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    /**
     * Test Transaction Transfer Money Business Exception - Sender account not found
     */
    @Test
    void TransactionService_Transfer_shouldReturnReturnBusinessException_whenSenderAccountNotFound() {

        // ARRANGE
        User senderUser = TestUserFactory.userWithId(1L);
        senderUser.setEmail("sender@sender.com");

        Account account = TestAccountFactory.accountWithId(1L);
        account.setUser(senderUser);

        // ACT
        when(accountRepository.findByUser_Email(senderUser.getEmail()))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionService.transfer(senderUser.getEmail(), account.getId(), new BigDecimal(100));
        });

        // ASSERT
        assertEquals("Sender account not found", exception.getMessage());

        verify(transactionRepository, never()).save(any());
    }

    /**
     * Test Transaction Transfer Money Business Exception - You cannot transfer money to your own account
     */
    @Test
    void TransactionService_Transfer_shouldReturnReturnBusinessException_whenYouCannotTransferMoneyToYourOwnAccount() {

        // ARRANGE
        User senderUser = TestUserFactory.userWithId(1L);
        senderUser.setEmail("sender@sender.com");

        Account senderAccount = TestAccountFactory.accountWithId(1L);
        senderAccount.setUser(senderUser);

        // Sender exists ✅
        when(accountRepository.findByUser_Email(senderUser.getEmail()))
                .thenReturn(Optional.of(senderAccount));

        // ACT
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionService.transfer(senderUser.getEmail(), senderAccount.getId(), new BigDecimal(100));
        });

        // ASSERT
        assertEquals("You cannot transfer money to your own account", exception.getMessage());

        verify(transactionRepository, never()).save(any());
    }


    /**
     * Test Transaction Transfer Money Business Exception - Receiver account not found
     */
    @Test
    void TransactionService_Transfer_shouldReturnReturnBusinessException_whenReceiverAccountNotFound() {

        // ARRANGE
        User senderUser = TestUserFactory.userWithId(1L);
        senderUser.setEmail("receiver@receiver.com");

        Account account = TestAccountFactory.accountWithId(1L);
        account.setUser(senderUser);

        Long receiverId = 999L;

        // ACT
        when(accountRepository.findByUser_Email(senderUser.getEmail()))
                .thenReturn(Optional.of(account));

        when(accountRepository.findById(receiverId))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            transactionService.transfer(senderUser.getEmail(), receiverId, new BigDecimal(100));
        });

        // ASSERT
        assertEquals("Receiver account not found", exception.getMessage());

        verify(transactionRepository, never()).save(any());
    }


}
