package com.example.tradingbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioDTO {
    private String symbol;
    private int quantity;
    private double currentPrice;
    private double totalValue;
}
