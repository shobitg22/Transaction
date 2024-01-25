package com.example.transactionManagement.service.impl;

import com.example.transactionManagement.exception.BadRequestException;
import com.example.transactionManagement.model.CurrencyType;
import com.example.transactionManagement.model.Entity.Transaction;
import com.example.transactionManagement.model.FxRatesApiResponse;
import com.example.transactionManagement.model.TransactionRequest;
import com.example.transactionManagement.model.TransactionType;
import com.example.transactionManagement.repo.TransactionRepo;
import com.example.transactionManagement.service.TransactionService;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceImplTest {

    private TransactionRepo transactionRepository;

    private RestTemplate restTemplate;

    private TransactionService transactionService;
    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        transactionRepository = Mockito.mock(TransactionRepo.class);
        restTemplate = Mockito.mock(RestTemplate.class);
        transactionService = new TransactionServiceImpl(transactionRepository,restTemplate);
    }

    @Test
    public void testGetAllTransactions() {
        when(transactionRepository.findAll()).thenReturn(Collections.emptyList());

        List<Transaction> result = transactionService.getAllTransactions();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(transactionRepository, times(1)).findAll();
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    public void testSaveTransaction() {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount(100.0);
        transactionRequest.setTransactionType(TransactionType.CREDIT.toString());
        transactionRequest.setCurrencyType(CurrencyType.USD.toString());

        FxRatesApiResponse fxRatesApiResponse = new FxRatesApiResponse();
        fxRatesApiResponse.setRates(Collections.singletonMap("INR", 75.0));

        when(restTemplate.getForObject(anyString(), eq(FxRatesApiResponse.class))).thenReturn(fxRatesApiResponse);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        Transaction result = transactionService.saveTransaction(transactionRequest);

        assertNotNull(result);

        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(restTemplate, times(1)).getForObject(anyString(), eq(FxRatesApiResponse.class));
        verifyNoMoreInteractions(transactionRepository, restTemplate);
    }

    @Test
    public void testSaveTransactionForNegativeAmount() {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount(-100.0);

        BadRequestException exception = assertThrows(BadRequestException.class,()->transactionService.saveTransaction(transactionRequest));
        assertEquals("Please enter a valid amount", exception.getMessage());

    }

    @Test
    public void testSaveTransactionForEmptyTransactionType() {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount(100.0);

        BadRequestException exception = assertThrows(BadRequestException.class,()->transactionService.saveTransaction(transactionRequest));
        assertEquals("TransactionType cannot be null", exception.getMessage());
    }

    @Test
    public void testSaveTransactionForEmptyCurrencyType() {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount(100.0);
        transactionRequest.setTransactionType(TransactionType.CREDIT.toString());

        BadRequestException exception = assertThrows(BadRequestException.class,()->transactionService.saveTransaction(transactionRequest));
        assertEquals("CurrencyType cannot be null", exception.getMessage());
    }

    @Test
    public void testSaveTransactionForInvalidTransactionType() {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount(100.0);
        transactionRequest.setTransactionType("Wrong");
        transactionRequest.setCurrencyType(CurrencyType.USD.toString());

        BadRequestException exception = assertThrows(BadRequestException.class,()->transactionService.saveTransaction(transactionRequest));
        assertEquals("Invalid transaction type, please enter CREDIT or DEBIT", exception.getMessage());
    }

    @Test
    public void testSaveTransactionForInvalidCurrencyType() {
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount(100.0);
        transactionRequest.setTransactionType(TransactionType.CREDIT.toString());
        transactionRequest.setCurrencyType("WRONG");

        BadRequestException exception = assertThrows(BadRequestException.class,()->transactionService.saveTransaction(transactionRequest));
        assertEquals("Invalid currency type, please enter USD or INR", exception.getMessage());
    }


    @Test
    public void testGetTransactionsByDateAndType() {
        LocalDate date = LocalDate.now();
        TransactionType type = TransactionType.CREDIT;

        when(transactionRepository.findByDateAndTransactionType(date, type)).thenReturn(Collections.emptyList());

        List<Transaction> result = transactionService.getTransactionsByDateAndType(date, type);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(transactionRepository, times(1)).findByDateAndTransactionType(date, type);
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    public void testGetTransactionCountByDay() {
        Transaction transaction1 = new Transaction();
        transaction1.setDate(LocalDate.now());
        transaction1.setTransactionType(TransactionType.CREDIT);

        Transaction transaction2 = new Transaction();
        transaction2.setDate(LocalDate.now());
        transaction2.setTransactionType(TransactionType.DEBIT);

        List<Transaction> allTransactions = Arrays.asList(transaction1, transaction2);

        when(transactionRepository.findAll()).thenReturn(allTransactions);

        var result = transactionService.getTransactionCountByDay();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(LocalDate.now()));

        Map<TransactionType, Long> countByType = result.get(LocalDate.now());
        assertEquals(2, countByType.size());
        assertEquals(1L, countByType.get(TransactionType.CREDIT));
        assertEquals(1L, countByType.get(TransactionType.DEBIT));

        verify(transactionRepository, times(1)).findAll();
        verifyNoMoreInteractions(transactionRepository);
    }





}
