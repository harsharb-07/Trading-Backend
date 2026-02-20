package com.example.tradingbackend.service;

import com.example.tradingbackend.dto.TradeRequest;
import com.example.tradingbackend.model.Portfolio;
import com.example.tradingbackend.model.Stock;
import com.example.tradingbackend.model.Transaction;
import com.example.tradingbackend.repository.PortfolioRepository;
import com.example.tradingbackend.repository.StockRepository;
import com.example.tradingbackend.repository.TransactionRepository;
import com.example.tradingbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradingService {

    private final TransactionRepository transactionRepository;
    private final PortfolioRepository portfolioRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;

    @Transactional
    public String buyStock(TradeRequest request) {
        // Validate user
        if (!userRepository.existsById(request.getUserId())) {
            throw new RuntimeException("User not found");
        }

        // Validate stock
        Optional<Stock> stockOpt = stockRepository.findBySymbol(request.getSymbol().toUpperCase());
        if (stockOpt.isEmpty()) {
            throw new RuntimeException("Stock symbol not found: " + request.getSymbol());
        }
        Stock stock = stockOpt.get();

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setUserId(request.getUserId());
        transaction.setSymbol(stock.getSymbol());
        transaction.setQuantity(request.getQuantity());
        transaction.setPrice(stock.getCurrentPrice());
        transaction.setType(Transaction.TransactionType.BUY);
        transaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction);

        // Update portfolio
        Optional<Portfolio> portfolioOpt = portfolioRepository.findByUserIdAndSymbol(request.getUserId(),
                stock.getSymbol());
        Portfolio portfolio;
        if (portfolioOpt.isPresent()) {
            portfolio = portfolioOpt.get();
            portfolio.setQuantity(portfolio.getQuantity() + request.getQuantity());
        } else {
            portfolio = new Portfolio();
            portfolio.setUserId(request.getUserId());
            portfolio.setSymbol(stock.getSymbol());
            portfolio.setQuantity(request.getQuantity());
        }
        portfolioRepository.save(portfolio);

        return "Successfully bought " + request.getQuantity() + " shares of " + stock.getSymbol();
    }

    @Transactional
    public String sellStock(TradeRequest request) {
        // Validate user
        if (!userRepository.existsById(request.getUserId())) {
            throw new RuntimeException("User not found");
        }

        // Validate stock
        Optional<Stock> stockOpt = stockRepository.findBySymbol(request.getSymbol().toUpperCase());
        if (stockOpt.isEmpty()) {
            throw new RuntimeException("Stock symbol not found: " + request.getSymbol());
        }
        Stock stock = stockOpt.get();

        // Check portfolio
        Optional<Portfolio> portfolioOpt = portfolioRepository.findByUserIdAndSymbol(request.getUserId(),
                stock.getSymbol());
        if (portfolioOpt.isEmpty() || portfolioOpt.get().getQuantity() < request.getQuantity()) {
            throw new RuntimeException("Insufficient shares to sell");
        }

        // Create transaction record
        Transaction transaction = new Transaction();
        transaction.setUserId(request.getUserId());
        transaction.setSymbol(stock.getSymbol());
        transaction.setQuantity(request.getQuantity());
        transaction.setPrice(stock.getCurrentPrice());
        transaction.setType(Transaction.TransactionType.SELL);
        transaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(transaction);

        // Update portfolio
        Portfolio portfolio = portfolioOpt.get();
        portfolio.setQuantity(portfolio.getQuantity() - request.getQuantity());

        if (portfolio.getQuantity() == 0) {
            portfolioRepository.delete(portfolio);
        } else {
            portfolioRepository.save(portfolio);
        }

        return "Successfully sold " + request.getQuantity() + " shares of " + stock.getSymbol();
    }

    public List<String> getTransactions(Long userId) {
        return transactionRepository.findByUserId(userId).stream()
                .map(t -> String.format("%s: %s %d %s @ $%.2f",
                        t.getTransactionDate(), t.getType(), t.getQuantity(), t.getSymbol(), t.getPrice()))
                .collect(Collectors.toList());
    }

    public List<com.example.tradingbackend.dto.PortfolioDTO> getPortfolio(Long userId) {
        return portfolioRepository.findByUserId(userId).stream()
                .map(p -> {
                    Optional<Stock> stockOpt = stockRepository.findBySymbol(p.getSymbol());
                    double price = stockOpt.map(Stock::getCurrentPrice).orElse(0.0);
                    return new com.example.tradingbackend.dto.PortfolioDTO(
                            p.getSymbol(),
                            p.getQuantity(),
                            price,
                            p.getQuantity() * price);
                })
                .collect(Collectors.toList());
    }

    public List<com.example.tradingbackend.dto.TransactionDTO> getRecentTransactions() {
        return transactionRepository.findTop10ByOrderByTransactionDateDesc().stream()
                .map(t -> {
                    String username = userRepository.findById(t.getUserId())
                            .map(com.example.tradingbackend.model.User::getUsername)
                            .orElse("Unknown User");

                    return new com.example.tradingbackend.dto.TransactionDTO(
                            username,
                            t.getSymbol(),
                            t.getQuantity(),
                            t.getPrice(),
                            t.getType().toString(),
                            t.getTransactionDate());
                })
                .collect(Collectors.toList());
    }
}
