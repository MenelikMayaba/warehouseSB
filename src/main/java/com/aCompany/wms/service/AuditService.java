package com.aCompany.wms.service;

import com.aCompany.wms.model.StockTransaction;
import com.aCompany.wms.model.TransactionType;
import com.aCompany.wms.repository.StockTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AuditService {

    @Autowired
    private StockTransactionRepository transactionRepository;


    public Page<StockTransaction> getAuditLogs(
            String user,
            String type,
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable) {

        if (user != null && !user.isEmpty()) {
            return transactionRepository.findByScannedBy(user, pageable);
        } else if (type != null && !type.isEmpty()) {
            try {
                return transactionRepository.findByType(TransactionType.valueOf(type), pageable);
            } catch (IllegalArgumentException e) {
                // Handle invalid type
                return transactionRepository.findAll(pageable);
            }
        } else if (startDate != null && endDate != null) {
            return transactionRepository.findByScannedAtBetween(startDate, endDate, pageable);
        } else {
            return transactionRepository.findAll(pageable);
        }
    }

    public void logEvent(String action, String details, String entityType, String entityId) {
        String username = "system";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            username = auth.getName();
        }

        StockTransaction log = new StockTransaction();
        log.setAction(action);
        log.setScannedBy(username);
        log.setScannedAt(LocalDateTime.now());
        log.setNotes(details);
        log.setType(TransactionType.SYSTEM);

        // You can set additional fields based on entityType and entityId
        // For example, if you have a reference to the entity, you can set it here

        transactionRepository.save(log);
    }

    public String exportToCsv(String user, String type, LocalDateTime startDate, LocalDateTime endDate) {
        // Get all matching logs without pagination

        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by("scannedAt").descending());

        Page<StockTransaction> logsPage = transactionRepository.search(
                user,
                type != null ? TransactionType.valueOf(type) : null,
                startDate,
                endDate,
                pageable
        );
        List<StockTransaction> logs = logsPage.getContent();


        // Create CSV header
        StringBuilder csv = new StringBuilder("ID,Timestamp,User,Action,Type,Details,Product,Quantity\n");

        // Add each log as a CSV row
        for (StockTransaction log : logs) {
            String productInfo = log.getProduct() != null ?
                    String.format("%s (%s)", log.getProduct().getName(), log.getProduct().getSku()) : "";

            String details = (log.getReferenceNumber() != null ? "Ref: " + log.getReferenceNumber() + " " : "") +
                    (log.getNotes() != null ? log.getNotes() : "");

            csv.append(String.format("%d,%s,%s,%s,%s,\"%s\",\"%s\",%d\n",
                    log.getId(),
                    log.getScannedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    log.getScannedBy(),
                    log.getAction(),
                    log.getType(),
                    details.replace("\"", "\"\""), // Escape quotes in details
                    productInfo.replace("\"", "\"\""), // Escape quotes in product info
                    log.getQuantity()
            ));
        }

        return csv.toString();
    }
}
