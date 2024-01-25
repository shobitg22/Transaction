package com.example.transactionManagement.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.transactionManagement.model.Entity.Transaction;
import com.example.transactionManagement.model.TransactionRequest;
import com.example.transactionManagement.model.TransactionType;
import com.example.transactionManagement.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
    }

    @Test
    public void testGetAllTransactions() throws Exception {
        when(transactionService.getAllTransactions()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/transactions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        verify(transactionService, times(1)).getAllTransactions();
        verifyNoMoreInteractions(transactionService);
    }

    @Test
    public void testSaveTransaction() throws Exception {
        TransactionRequest transactionRequest = new TransactionRequest(); // Create a valid request

        when(transactionService.saveTransaction(any(TransactionRequest.class)))
            .thenReturn(new Transaction()); // Create a mock response

        mockMvc.perform(MockMvcRequestBuilders.post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(transactionRequest)))
            .andExpect(status().isOk());

        verify(transactionService, times(1)).saveTransaction(any(TransactionRequest.class));
        verifyNoMoreInteractions(transactionService);
    }

    @Test
    public void testGetTransactionsByDateAndType() throws Exception {
        LocalDate date = LocalDate.now();
        TransactionType type = TransactionType.CREDIT;

        when(transactionService.getTransactionsByDateAndType(date, type)).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/transactions/filter")
                .param("date", date.toString())
                .param("type", type.name()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        verify(transactionService, times(1)).getTransactionsByDateAndType(date, type);
        verifyNoMoreInteractions(transactionService);
    }

    @Test
    public void testGetTransactionCountByDay() throws Exception {
        Map<LocalDate, Map<TransactionType, Long>> countMap = new HashMap<>(); // Create a mock response

        when(transactionService.getTransactionCountByDay()).thenReturn(countMap);

        mockMvc.perform(MockMvcRequestBuilders.get("/transactions/filterByDate"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isMap());

        verify(transactionService, times(1)).getTransactionCountByDay();
        verifyNoMoreInteractions(transactionService);
    }
}
