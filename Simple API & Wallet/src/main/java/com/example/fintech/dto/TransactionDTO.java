package com.example.fintech.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction Data Transfer Object
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO implements Serializable {

    private Long id;
    private BigDecimal balance;
    private LocalDateTime timestamp;
    private Long senderId;
    private Long receiverId;
    private String operationType;
    private String counterpartyEmail;

}
