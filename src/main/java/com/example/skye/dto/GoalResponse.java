package com.example.skye.dto;

import com.example.skye.util.JsonMoneySerializer;
import com.example.skye.util.JsonProgressMoneySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class GoalResponse {
    private Long id;
    private String goalName;

    @JsonSerialize(using = JsonMoneySerializer.class)
    private BigDecimal targetAmount;

    private LocalDate targetDate;
    private LocalDate startDate;

    @JsonSerialize(using = JsonProgressMoneySerializer.class)
    private BigDecimal currentProgress;

    private Double progressPercentage;

    @JsonSerialize(using = JsonMoneySerializer.class)
    private BigDecimal remainingAmount;
}
