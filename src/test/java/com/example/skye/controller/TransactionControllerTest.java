package com.example.skye.controller;

import com.example.skye.dto.MessageResponse;
import com.example.skye.dto.TransactionRequest;
import com.example.skye.dto.TransactionResponse;
import com.example.skye.dto.TransactionUpdateRequest;
import com.example.skye.dto.TransactionsListResponse;
import com.example.skye.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;
    @InjectMocks
    private TransactionController transactionController;

    @Test
    void create_returnsCreated() {
        TransactionResponse body = TransactionResponse.builder().id(1L).type("INCOME").build();
        when(transactionService.create(org.mockito.ArgumentMatchers.any(TransactionRequest.class))).thenReturn(body);

        ResponseEntity<TransactionResponse> response = transactionController.create(new TransactionRequest());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void getAll_returnsOk() {
        when(transactionService.getAll(null, null, null, null, null))
                .thenReturn(new TransactionsListResponse(List.of()));

        assertEquals(HttpStatus.OK, transactionController.getAll(null, null, null, null, null).getStatusCode());
    }

    @Test
    void update_returnsOk() {
        TransactionResponse body = TransactionResponse.builder().id(1L).build();
        when(transactionService.update(1L, new TransactionUpdateRequest())).thenReturn(body);

        assertEquals(HttpStatus.OK, transactionController.update(1L, new TransactionUpdateRequest()).getStatusCode());
    }

    @Test
    void delete_returnsOk() {
        when(transactionService.delete(1L)).thenReturn(new MessageResponse("Transaction deleted successfully"));
        assertEquals(HttpStatus.OK, transactionController.delete(1L).getStatusCode());
    }
}
