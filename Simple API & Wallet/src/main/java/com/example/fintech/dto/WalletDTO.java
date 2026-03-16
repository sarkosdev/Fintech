package com.example.fintech.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Wallet Data Transfer Object
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletDTO {

    private String email;
    private Long accountId;

}
