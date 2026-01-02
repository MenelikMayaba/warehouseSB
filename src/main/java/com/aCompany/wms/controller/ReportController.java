package com.aCompany.wms.controller;

import com.aCompany.wms.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/inventory-summary")
    public ResponseEntity<Map<String, Object>> getInventorySummary() {
        return ResponseEntity.ok(reportService.getInventorySummary());
    }

    @GetMapping("/inventory-valuation")
    public ResponseEntity<Map<String, Object>> getInventoryValuation() {
        return ResponseEntity.ok(reportService.getInventoryValuationReport());
    }

    @GetMapping("/movement")
    public ResponseEntity<Map<String, Object>> getMovementReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(reportService.getMovementReport(startDate, endDate));
    }
}