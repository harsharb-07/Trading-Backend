package com.example.tradingbackend.controller;

import com.example.tradingbackend.dto.StockQuote;
import com.example.tradingbackend.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
@Tag(name = "Stock Market", description = "APIs for retrieving stock market data")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/quote/{symbol}")
    @Operation(summary = "Get stock quote")
    public StockQuote getStockQuote(@PathVariable String symbol) {
        return stockService.getQuote(symbol);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all stocks")
    public java.util.List<com.example.tradingbackend.model.Stock> getAllStocks() {
        return stockService.getAllStocks();
    }

    @GetMapping("/history/{symbol}/{timeframe}")
    @Operation(summary = "Get stock history graph data")
    public java.util.List<com.example.tradingbackend.dto.StockHistoryDTO> getStockHistory(
            @PathVariable String symbol,
            @PathVariable String timeframe) {
        return stockService.getStockHistory(symbol, timeframe);
    }
}
