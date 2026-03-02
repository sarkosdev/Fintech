package com.example.fintech.service;

import com.example.fintech.entity.Account;
import com.example.fintech.entity.Transaction;
import com.example.fintech.exception.BusinessException;
import com.example.fintech.repository.AccountRepository;
import com.example.fintech.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor    // Creates constructor for dependency injection
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    /**
     * O @Transactional cria uma "unidade de trabalho".
     * Se QUALQUER RuntimeException acontecer aqui dentro,
     * o banco de dados volta ao estado anterior (Rollback).
     */
    @Transactional
    public Transaction transfer(Long senderId, Long receiverId, BigDecimal amount) {

        // 1. Validar se não é a mesma conta
        if (senderId.equals(receiverId)) {
            throw new RuntimeException("Não é possível transferir para a própria conta.");
        }

        // 2. Buscar as contas (Usamos findById para garantir que temos o saldo atualizado)
        Account sender = accountRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Conta de origem não encontrada"));

        Account receiver = accountRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Conta de destino não encontrada"));

        // Verifica se quem envia tem dinheiro suficiente, caso nao tenha, dispara excepcao
        if(sender.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("Saldo insuficiente para completar a transferência.");
        }

        // 3. Executar a lógica de negócio (Débito e Crédito)
        sender.withdraw(amount);
        receiver.deposit(amount);

        // 4. Salvar as alterações de saldo
        accountRepository.save(sender);
        accountRepository.save(receiver);

        // 5. Registrar o histórico da transação
        Transaction tx = new Transaction();
        tx.setSenderAccount(sender);
        tx.setReceiverAccount(receiver);
        tx.setBalance(amount);

        return transactionRepository.save(tx);

        // Se o código chegar aqui com sucesso, o Spring faz o "Commit" no banco.
    }


}
