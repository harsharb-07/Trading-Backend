package com.example.tradingbackend.service;

import com.example.tradingbackend.dto.StockQuote;
import com.example.tradingbackend.model.Stock;
import com.example.tradingbackend.repository.StockRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    @PostConstruct
    public void init() {
        if (stockRepository.count() == 0) {
            seedStock("RELIANCE", "Reliance Industries Ltd.", 2456.75, 3.45);
            seedStock("TCS", "Tata Consultancy Services", 3789.20, 2.89);
            seedStock("INFY", "Infosys Limited", 1654.30, 2.67);
            seedStock("HDFCBANK", "HDFC Bank Limited", 1589.90, 2.34);
            seedStock("WIPRO", "Wipro Limited", 456.85, 2.15);
            seedStock("TATAMOTORS", "Tata Motors Limited", 789.45, -3.21);
            seedStock("BHARTIARTL", "Bharti Airtel Limited", 934.20, -2.87);
            seedStock("AXISBANK", "Axis Bank Limited", 1023.45, -2.45);
            seedStock("ICICIBANK", "ICICI Bank Limited", 1089.30, -2.12);
            seedStock("SBIN", "State Bank of India", 623.75, -1.98);
        }
    }

    private void seedStock(String symbol, String companyName, double price, double change) {
        Stock stock = new Stock();
        stock.setSymbol(symbol);
        stock.setCompanyName(companyName);
        stock.setCurrentPrice(price);
        stock.setChangeAmount(change);
        stock.setChangePercentage((change / price) * 100);
        stockRepository.save(stock);
    }

    public StockQuote getQuote(String symbol) {
        Optional<Stock> stockOpt = stockRepository.findBySymbol(symbol.toUpperCase());

        if (stockOpt.isPresent()) {
            Stock stock = stockOpt.get();
            return new StockQuote(stock.getSymbol(), stock.getCurrentPrice(), stock.getChangeAmount());
        } else {
            return new StockQuote(symbol.toUpperCase(), 0.0, 0.0);
        }
    }

    public java.util.List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public java.util.List<com.example.tradingbackend.dto.StockHistoryDTO> getStockHistory(String symbol,
            String timeframe) {
        // Find current price to base history on
        double currentPrice = getQuote(symbol).getPrice();
        if (currentPrice == 0)
            currentPrice = 100.0; // Fallback

        java.util.List<com.example.tradingbackend.dto.StockHistoryDTO> history = new java.util.ArrayList<>();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        int points;
        int intervalMinutes;

        // Determine points and interval based on timeframe
        switch (timeframe.toUpperCase()) {
            case "1D":
                points = 24; // Hourly
                intervalMinutes = 60;
                break;
            case "1W":
                points = 7; // Daily
                intervalMinutes = 24 * 60;
                break;
            case "1M":
                points = 30; // Daily
                intervalMinutes = 24 * 60;
                break;
            case "1Y":
                points = 12; // Monthly
                intervalMinutes = 30 * 24 * 60;
                break;
            default:
                points = 24;
                intervalMinutes = 60;
        }

        // Generate data walking backwards from current price
        // To make graph look realistic ending at current price, we generate backwards
        double tempPrice = currentPrice;
        for (int i = 0; i < points; i++) {
            // Apply random volatility (-2% to +2%)
            double volatility = (Math.random() * 0.04) - 0.02;
            double openPrice = tempPrice / (1 + volatility);

            // We want the list to be chronological (oldest to newest)
            // Since we're walking backwards, calculate price[i] relative to price[i-1]
            // logic reversed
            // Actually, simplest is: generate a random walk entirely, then scale/shift to
            // end at currentPrice?
            // Or just walk backwards: previous_price = current_price / (1 + change)

            java.time.LocalDateTime timePoint = now.minusMinutes((long) i * intervalMinutes);
            history.add(new com.example.tradingbackend.dto.StockHistoryDTO(timePoint.toString(), tempPrice));

            tempPrice = openPrice; // Move to previous time step's price
        }

        // Sort chronologically (oldest first)
        java.util.Collections.reverse(history);
        return history;
    }
}
