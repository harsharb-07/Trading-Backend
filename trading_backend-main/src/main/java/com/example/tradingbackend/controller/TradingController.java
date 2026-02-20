package com.example.tradingbackend.controller;

import com.example.tradingbackend.dto.TradeRequest;
import com.example.tradingbackend.service.TradingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trading")
@Tag(name = "Trading Operations")
public class TradingController {

    private final TradingService tradingService;

    public TradingController(TradingService tradingService) {
        this.tradingService = tradingService;
    }

    @PostMapping("/buy")
    @Operation(summary = "Buy stock")
    public String buy(@RequestBody TradeRequest request) {
        return tradingService.buyStock(request);
    }

    @PostMapping("/sell")
    @Operation(summary = "Sell stock")
    public String sell(@RequestBody TradeRequest request) {
        return tradingService.sellStock(request);
    }

    @GetMapping("/transactions/{userId}")
    @Operation(summary = "Get transaction history")
    public List<String> transactions(@PathVariable Long userId) {
        return tradingService.getTransactions(userId);
    }

    @GetMapping("/portfolio/{userId}")
    @Operation(summary = "Get user portfolio")
    public List<com.example.tradingbackend.dto.PortfolioDTO> portfolio(@PathVariable Long userId) {
        return tradingService.getPortfolio(userId);
    }

    @GetMapping("/feed")
    @Operation(summary = "Get live trade feed")
    public List<com.example.tradingbackend.dto.TransactionDTO> getFeed() {
        return tradingService.getRecentTransactions();
    }
}
