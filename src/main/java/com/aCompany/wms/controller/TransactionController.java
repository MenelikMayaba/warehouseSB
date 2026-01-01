package com.aCompany.wms.controller;

import com.aCompany.wms.model.StockTransaction;
import com.aCompany.wms.model.TransactionType;
import com.aCompany.wms.repository.StockTransactionRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@PreAuthorize("hasRole('ADMIN')")
public class TransactionController {
    private final StockTransactionRepository transactionRepository;

    public TransactionController(StockTransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/product/{productId}")
    public List<StockTransaction> getProductTransactions(@PathVariable Long productId) {
        return transactionRepository.findByProductIdOrderByScannedAtDesc(productId);
    }

    @GetMapping("/user/{username}")
    public List<StockTransaction> getUserTransactions(@PathVariable String username) {
        return transactionRepository.findByScannedByOrderByScannedAtDesc(username);
    }

    @GetMapping("/type/{type}")
    public List<StockTransaction> getTransactionsByType(@PathVariable TransactionType type) {
        return transactionRepository.findByTypeOrderByScannedAtDesc(type);
    }
}