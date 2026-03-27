package com.example.fintech.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Data Transfer Object - ConfirmationCodeDTO
 * Object used to send the User confirmation code as a response
 */
@AllArgsConstructor
@Getter
public class ConfirmationCodeDTO {

    private String userCode;
}
