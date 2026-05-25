package com.example.skye.service;

import com.example.skye.dto.MonthlyReportResponse;
import com.example.skye.dto.YearlyReportResponse;
import com.example.skye.entity.TransactionEntity;
import com.example.skye.entity.UserEntity;
import com.example.skye.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds monthly and yearly financial reports from persisted transactions.
 */
@Service
public class ReportService {

    private final TransactionRepository transactionRepository;
    private final CurrentUserService currentUserService;

    public ReportService(TransactionRepository transactionRepository, CurrentUserService currentUserService) {
        this.transactionRepository = transactionRepository;
        this.currentUserService = currentUserService;
    }

    public MonthlyReportResponse getMonthlyReport(int year, int month) {
        validateMonth(month);
        UserEntity user = currentUserService.getCurrentUser();
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        List<TransactionEntity> transactions = transactionRepository.findByUserAndDateRange(
                user.getId(), start, end);

        return buildMonthlyResponse(month, year, transactions);
    }

    public YearlyReportResponse getYearlyReport(int year) {
        UserEntity user = currentUserService.getCurrentUser();
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        List<TransactionEntity> transactions = transactionRepository.findByUserAndDateRange(
                user.getId(), start, end);

        Map<String, BigDecimal> income = new LinkedHashMap<>();
        Map<String, BigDecimal> expenses = new LinkedHashMap<>();
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (TransactionEntity transaction : transactions) {
            String categoryName = transaction.getCategory().getName();
            if ("INCOME".equals(transaction.getCategory().getType())) {
                income.merge(categoryName, transaction.getAmount(), BigDecimal::add);
                totalIncome = totalIncome.add(transaction.getAmount());
            } else {
                expenses.merge(categoryName, transaction.getAmount(), BigDecimal::add);
                totalExpense = totalExpense.add(transaction.getAmount());
            }
        }

        return YearlyReportResponse.builder()
                .year(year)
                .totalIncome(income)
                .totalExpenses(expenses)
                .netSavings(totalIncome.subtract(totalExpense))
                .build();
    }

    private MonthlyReportResponse buildMonthlyResponse(int month, int year, List<TransactionEntity> transactions) {
        Map<String, BigDecimal> income = new LinkedHashMap<>();
        Map<String, BigDecimal> expenses = new LinkedHashMap<>();
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (TransactionEntity transaction : transactions) {
            String categoryName = transaction.getCategory().getName();
            if ("INCOME".equals(transaction.getCategory().getType())) {
                income.merge(categoryName, transaction.getAmount(), BigDecimal::add);
                totalIncome = totalIncome.add(transaction.getAmount());
            } else {
                expenses.merge(categoryName, transaction.getAmount(), BigDecimal::add);
                totalExpense = totalExpense.add(transaction.getAmount());
            }
        }

        return MonthlyReportResponse.builder()
                .month(month)
                .year(year)
                .totalIncome(income)
                .totalExpenses(expenses)
                .netSavings(totalIncome.subtract(totalExpense))
                .build();
    }

    private void validateMonth(int month) {
        if (month < 1 || month > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Month must be between 1 and 12");
        }
    }
}
