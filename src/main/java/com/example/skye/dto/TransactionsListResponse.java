package com.example.skye.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class TransactionsListResponse {
    private List<TransactionResponse> transactions = new ArrayList<>();

    public TransactionsListResponse(List<TransactionResponse> transactions) {
        this.transactions = transactions != null ? transactions : new ArrayList<>();
    }
}
