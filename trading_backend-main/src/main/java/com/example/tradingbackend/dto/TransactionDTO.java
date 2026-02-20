package com.example.tradingbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private String username;
    private String symbol;
    private int quantity;
    private double price;
    private String type; // BUY or SELL
    private LocalDateTime timestamp;
}
