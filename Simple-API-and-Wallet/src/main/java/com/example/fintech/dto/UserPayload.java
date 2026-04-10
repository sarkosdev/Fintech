package com.example.fintech.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Object that holds User payload data for Outbox Pattern Design
 */
@NoArgsConstructor
@Data
public class UserPayload {
    private Long id;
    private String email;
}
