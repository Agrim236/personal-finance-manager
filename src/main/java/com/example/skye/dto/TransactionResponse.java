package com.example.skye.dto;

import com.example.skye.util.JsonMoneySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class TransactionResponse {
    private Long id;

    @JsonSerialize(using = JsonMoneySerializer.class)
    private BigDecimal amount;
    private LocalDate date;
    private String category;
    private String description;
    private String type;
}
