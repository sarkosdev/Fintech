package com.example.fintech.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * LoginResponse Data Transfer Object
 */
@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
}
