package com.example.tradingbackend.dto;

public class StockQuote {

    private String symbol;
    private double price;
    private double change;

    public StockQuote(String symbol, double price, double change) {
        this.symbol = symbol;
        this.price = price;
        this.change = change;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    public double getChange() {
        return change;
    }
}
