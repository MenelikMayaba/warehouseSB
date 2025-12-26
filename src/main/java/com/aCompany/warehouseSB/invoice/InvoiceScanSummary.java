package com.aCompany.warehouseSB.invoice;

import java.time.LocalDateTime;

public class InvoiceScanSummary {

    private Long invoiceId;
    private String cageCode;
    private int totalRequired;
    private int totalScanned;

    private String lastScannedBy;
    private LocalDateTime lastScannedAt;

    public InvoiceScanSummary(
            Long invoiceId,
            String cageCode,
            int totalRequired,
            int totalScanned,
            String lastScannedBy,
            LocalDateTime lastScannedAt
    ) {
        this.invoiceId = invoiceId;
        this.cageCode = cageCode;
        this.totalRequired = totalRequired;
        this.totalScanned = totalScanned;
        this.lastScannedBy = lastScannedBy;
        this.lastScannedAt = lastScannedAt;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public String getCageCode() {
        return cageCode;
    }

    public int getTotalRequired() {
        return totalRequired;
    }

    public int getTotalScanned() {
        return totalScanned;
    }

    public String getLastScannedBy() {
        return lastScannedBy;
    }

    public LocalDateTime getLastScannedAt() {
        return lastScannedAt;
    }
}
