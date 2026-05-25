package com.example.skye.dto;

import com.example.skye.util.JsonMoneySerializer;
import com.example.skye.util.JsonNetSavingsSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class MonthlyReportResponse {
    private int month;
    private int year;

    @JsonSerialize(contentUsing = JsonMoneySerializer.class)
    private Map<String, BigDecimal> totalIncome;

    @JsonSerialize(contentUsing = JsonMoneySerializer.class)
    private Map<String, BigDecimal> totalExpenses;

    @JsonSerialize(using = JsonNetSavingsSerializer.class)
    private BigDecimal netSavings;
}
