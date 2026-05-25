package com.example.skye.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionUpdateRequest {

    @DecimalMin(value = "0.01", message = "Amount must be a positive value")
    private BigDecimal amount;

    private String category;

    private String description;

    /** Accepted in requests but never applied — transaction date is immutable. */
    private LocalDate date;
}
