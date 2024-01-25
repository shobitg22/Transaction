package com.example.transactionManagement.service;

import com.example.transactionManagement.model.Entity.Transaction;
import com.example.transactionManagement.model.TransactionRequest;
import com.example.transactionManagement.model.TransactionType;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface TransactionService {

    List<Transaction> getAllTransactions();

    Transaction saveTransaction(TransactionRequest transaction);

    List<Transaction> getTransactionsByDateAndType(LocalDate date, TransactionType type);

    // Get transaction count by day and type
    Map<LocalDate, Map<TransactionType, Long>> getTransactionCountByDay();
}
