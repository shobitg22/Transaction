package com.example.transactionManagement.model.Entity;

import com.example.transactionManagement.model.TransactionRequest;
import com.example.transactionManagement.model.TransactionType;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private double amount;
    private LocalDate date; // New field to represent the date of the transaction

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;


    public Transaction() {

    }

    public Transaction(TransactionRequest transactionRequest) {
        this.description = transactionRequest.getDescription();
        this.transactionType = Objects.equals(transactionRequest.getTransactionType(), "CREDIT") ?
            TransactionType.CREDIT : TransactionType.DEBIT;
        this.amount = transactionRequest.getAmount();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    private <E extends Enum<E>> boolean isValidEnumValue(Class<E> enumClass, String value) {
        return Arrays.stream(enumClass.getEnumConstants())
            .anyMatch(enumValue -> enumValue.name().equals(value));
    }
}
