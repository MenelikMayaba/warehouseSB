package com.aCompany.wms.controller;

import com.aCompany.wms.model.StockTransaction;
import com.aCompany.wms.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/audit")
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @GetMapping
    public String viewAuditLogs(
            @RequestParam(required = false) String user,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("scannedAt").descending());

        // Parse dates if provided
        LocalDateTime start = null;
        LocalDateTime end = null;
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

        try {
            if (startDate != null && !startDate.isEmpty()) {
                start = LocalDateTime.parse(startDate, formatter);
            }
            if (endDate != null && !endDate.isEmpty()) {
                end = LocalDateTime.parse(endDate, formatter);
            }
        } catch (Exception e) {
            // Handle parse exception
        }

        Page<StockTransaction> auditPage = auditService.getAuditLogs(user, type, start, end, pageable);

        model.addAttribute("auditLogs", auditPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", auditPage.getTotalPages());
        model.addAttribute("totalItems", auditPage.getTotalElements());
        model.addAttribute("userFilter", user);
        model.addAttribute("typeFilter", type);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "admin/audit-logs";
    }

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<Page<StockTransaction>> getAuditLogs(
            @RequestParam(required = false) String user,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("scannedAt").descending());
        Page<StockTransaction> logs = auditService.getAuditLogs(user, type, startDate, endDate, pageable);
        return ResponseEntity.ok(logs);
    }
    @ExceptionHandler(Exception.class)
    public String handleError(Model model, Exception ex) {
        model.addAttribute("error", "An error occurred while retrieving audit logs: " + ex.getMessage());
        return "error"; // Make sure you have an error.html template
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportAuditLogs(
            @RequestParam(required = false) String user,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        String csvContent = auditService.exportToCsv(user, type, startDate, endDate);

        byte[] csvBytes = csvContent.getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "audit-logs-" +
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ".csv");

        return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
    }

}