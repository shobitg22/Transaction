package com.example.transactionManagement.repo;

import com.example.transactionManagement.model.Entity.Transaction;
import com.example.transactionManagement.model.TransactionType;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    List<Transaction> findByDateAndTransactionType(LocalDate date, TransactionType type);
}
