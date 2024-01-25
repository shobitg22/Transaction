package com.example.transactionManagement.controller;

import com.example.transactionManagement.model.Entity.Transaction;
import com.example.transactionManagement.model.TransactionRequest;
import com.example.transactionManagement.model.TransactionType;
import com.example.transactionManagement.service.TransactionService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @PostMapping
    public Transaction saveTransaction(@RequestBody TransactionRequest transaction) {
        return transactionService.saveTransaction(transaction);
    }

    @GetMapping("/filter")
    public List<Transaction> getTransactionsByDateAndType(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @RequestParam TransactionType type) {
        return transactionService.getTransactionsByDateAndType(date, type);
    }

    @GetMapping("/filterByDate")
    public Map<LocalDate, Map<TransactionType, Long>> getTransactionCountByDay() {
        return transactionService.getTransactionCountByDay();
    }
}
