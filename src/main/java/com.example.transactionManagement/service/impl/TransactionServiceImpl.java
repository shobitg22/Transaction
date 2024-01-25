package com.example.transactionManagement.service.impl;

import com.example.transactionManagement.exception.BadRequestException;
import com.example.transactionManagement.model.CurrencyType;
import com.example.transactionManagement.model.Entity.Transaction;
import com.example.transactionManagement.model.FxRatesApiResponse;
import com.example.transactionManagement.model.TransactionRequest;
import com.example.transactionManagement.model.TransactionType;
import com.example.transactionManagement.repo.TransactionRepo;
import com.example.transactionManagement.service.TransactionService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepo transactionRepository;

    private static final String FX_API_URL = "https://api.fxratesapi.com/latest/";

    private final RestTemplate restTemplate;

    public TransactionServiceImpl(TransactionRepo transactionRepository, RestTemplate restTemplate) {
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction saveTransaction(TransactionRequest transaction) {
        validateTransaction(transaction);
        Transaction mainTransaction = new Transaction(transaction);
        mainTransaction.setDate(LocalDate.now());
        if (transaction.getCurrencyType().equals(CurrencyType.USD.toString())) {
            mainTransaction.setAmount(convertUsdToInr(mainTransaction.getAmount()));
        }
        return transactionRepository.save(mainTransaction);
    }

    private void validateTransaction(TransactionRequest transaction) {

        if (transaction.getAmount() < 0.0) {
            throw new BadRequestException("Please enter a valid amount");
        }

        if (transaction.getTransactionType() == null) {
            throw new BadRequestException("TransactionType cannot be null");
        }

        if (transaction.getCurrencyType() == null) {
            throw new BadRequestException("CurrencyType cannot be null");
        }
        if (isValidEnum(TransactionType.class, transaction.getTransactionType())) {
            throw new BadRequestException("Invalid transaction type, please enter CREDIT or DEBIT");
        }
        if (isValidEnum(CurrencyType.class, transaction.getCurrencyType())) {
            throw new BadRequestException("Invalid currency type, please enter USD or INR");
        }
    }

    private <T extends Enum<T>> boolean isValidEnum(Class<T> enumClass, String value) {
        try {
            Enum.valueOf(enumClass, value);
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    private double convertUsdToInr(double amount) {
        FxRatesApiResponse response = restTemplate.getForObject(FX_API_URL, FxRatesApiResponse.class);

        if (response != null && response.getRates() != null) {
            return amount * response.getRates().getOrDefault("INR", 1.0);
        } else {
            throw new RuntimeException("Error fetching exchange rates from API");
        }
    }

    @Override
    public List<Transaction> getTransactionsByDateAndType(LocalDate date, TransactionType type) {
        return transactionRepository.findByDateAndTransactionType(date, type);
    }

    @Override
    public Map<LocalDate, Map<TransactionType, Long>> getTransactionCountByDay() {
        List<Transaction> allTransactions = transactionRepository.findAll();

       return allTransactions.stream()
            .collect(Collectors.groupingBy(Transaction::getDate,
                Collectors.groupingBy(Transaction::getTransactionType, Collectors.counting())));

    }
}
