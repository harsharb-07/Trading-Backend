package com.example.tradingbackend.repository;

import com.example.tradingbackend.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);

    // Fetch latest 10 transactions for the social feed
    List<Transaction> findTop10ByOrderByTransactionDateDesc();
}
