package com.example.skye.controller;

import com.example.skye.dto.MonthlyReportResponse;
import com.example.skye.dto.YearlyReportResponse;
import com.example.skye.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<MonthlyReportResponse> monthly(@PathVariable int year, @PathVariable int month) {
        return ResponseEntity.ok(reportService.getMonthlyReport(year, month));
    }

    @GetMapping("/yearly/{year}")
    public ResponseEntity<YearlyReportResponse> yearly(@PathVariable int year) {
        return ResponseEntity.ok(reportService.getYearlyReport(year));
    }
}
