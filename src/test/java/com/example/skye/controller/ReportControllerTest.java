package com.example.skye.controller;

import com.example.skye.dto.MonthlyReportResponse;
import com.example.skye.dto.YearlyReportResponse;
import com.example.skye.service.ReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private ReportService reportService;
    @InjectMocks
    private ReportController reportController;

    @Test
    void monthly_returnsOk() {
        MonthlyReportResponse body = MonthlyReportResponse.builder()
                .month(1).year(2024)
                .totalIncome(Map.of()).totalExpenses(Map.of())
                .netSavings(BigDecimal.ZERO)
                .build();
        when(reportService.getMonthlyReport(2024, 1)).thenReturn(body);
        assertEquals(HttpStatus.OK, reportController.monthly(2024, 1).getStatusCode());
    }

    @Test
    void yearly_returnsOk() {
        YearlyReportResponse body = YearlyReportResponse.builder()
                .year(2024)
                .totalIncome(Map.of()).totalExpenses(Map.of())
                .netSavings(BigDecimal.ZERO)
                .build();
        when(reportService.getYearlyReport(2024)).thenReturn(body);
        assertEquals(HttpStatus.OK, reportController.yearly(2024).getStatusCode());
    }
}
