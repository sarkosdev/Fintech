package com.example.fintech.entity;

import com.example.fintech.exception.BusinessException;
import lombok.*;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/**
 * This class maps the DB Entity 'Account'
 */
@Entity
@Data
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)                                       // Block accesses to 'id' setter
    private Long id;

    @NotNull
    @PositiveOrZero(message = "Field 'balance' in Account cannot be NEGATIVE")
    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;



    //---------------- ACOUNT OPERATIONS --------------------
    // DOMAIN DRIVEN DESIGN - APPROACH
    // DOMAIN KNOWS ITS RULES AND OPERATIONS, SERVICE ONLY USE THEM

    /**
     * Allows User to withdraw from this Account if the ammount is equals or lower then
     * the account current balance
     * @param amount
     */
    public void withdraw(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new BusinessException("Not enough balance in this account " + this.id);
        }
        this.balance = this.balance.subtract(amount);
    }

    /**
     * Allows User to deposit money in this Account
     * @param amount
     */
    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }


}
