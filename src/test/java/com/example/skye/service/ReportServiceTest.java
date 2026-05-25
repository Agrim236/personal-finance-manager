package com.example.skye.service;

import com.example.skye.dto.MonthlyReportResponse;
import com.example.skye.dto.YearlyReportResponse;
import com.example.skye.entity.CategoryEntity;
import com.example.skye.entity.TransactionEntity;
import com.example.skye.entity.UserEntity;
import com.example.skye.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private CurrentUserService currentUserService;
    @InjectMocks
    private ReportService reportService;

    @Test
    void getMonthlyReport_invalidMonth_throwsBadRequest() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        when(currentUserService.getCurrentUser()).thenReturn(user);

        assertThrows(ResponseStatusException.class, () -> reportService.getMonthlyReport(2024, 13));
        assertThrows(ResponseStatusException.class, () -> reportService.getMonthlyReport(2024, 0));
    }

    @Test
    void getMonthlyReport_emptyMonth_zeroNetSavings() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(transactionRepository.findByUserAndDateRange(eq(1L), any(), any()))
                .thenReturn(Collections.emptyList());

        MonthlyReportResponse report = reportService.getMonthlyReport(2024, 12);

        assertEquals(0, report.getNetSavings().compareTo(BigDecimal.ZERO));
    }

    @Test
    void getMonthlyReport_aggregatesIncomeAndExpenses() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        CategoryEntity salary = new CategoryEntity();
        salary.setName("Salary");
        salary.setType("INCOME");
        CategoryEntity food = new CategoryEntity();
        food.setName("Food");
        food.setType("EXPENSE");

        TransactionEntity income = new TransactionEntity();
        income.setAmount(new BigDecimal("5000.00"));
        income.setCategory(salary);
        TransactionEntity expense = new TransactionEntity();
        expense.setAmount(new BigDecimal("400.00"));
        expense.setCategory(food);

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(transactionRepository.findByUserAndDateRange(eq(1L), any(), any()))
                .thenReturn(List.of(income, expense));

        MonthlyReportResponse report = reportService.getMonthlyReport(2024, 1);
        assertEquals(new BigDecimal("6550.00"), report.getNetSavings());
        assertEquals(new BigDecimal("5000.00"), report.getTotalIncome().get("Salary"));
        assertEquals(new BigDecimal("400.00"), report.getTotalExpenses().get("Food"));
    }

    @Test
    void getYearlyReport_aggregatesTransactions() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        CategoryEntity salary = new CategoryEntity();
        salary.setName("Salary");
        salary.setType("INCOME");
        TransactionEntity income = new TransactionEntity();
        income.setAmount(new BigDecimal("3000.00"));
        income.setCategory(salary);

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(transactionRepository.findByUserAndDateRange(eq(1L), any(), any()))
                .thenReturn(List.of(income));

        YearlyReportResponse report = reportService.getYearlyReport(2024);
        assertEquals(2024, report.getYear());
        assertEquals(new BigDecimal("3000.00"), report.getNetSavings());
    }
}
