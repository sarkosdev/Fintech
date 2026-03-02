package com.example.fintech.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * LoginRequest Data Transfer Object
 */
@Data
@AllArgsConstructor
public class LoginRequestDTO {

    private String email;
    private String password;

}
