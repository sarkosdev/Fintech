package com.example.fintech.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * RequestEvent - Message sent/received over Kafka topic defined
 * Used by Producer and Consumer
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestEvent {

    private Long userId;
    private String userEmail;
    private BigDecimal giveAwayFreeAmount;

}
