package com.example.fintech.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * This class maps the DB Entity 'Transaction'
 */
@Data
@Entity
@Table( name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)                                       // Block accesses to 'id' setter
    private Long id;

    @NotNull(message = "Field 'balance' in Transaction cannot be empty")
    @Positive(message = "Field 'balance' in Transaction must be positive")
    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime timestamp;

    // Relacionamento com a conta de origem
    @NotNull(message = "Field 'senderAccountId' cannot be empty")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Account senderAccount;

    // Relacionamento com a conta de destino
    @NotNull(message = "Field 'receiverAccountId' cannot be empty")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Account receiverAccount;


    /**
     * Automatically generates the 'transaction_date' right before inserting in DB
     */
    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
