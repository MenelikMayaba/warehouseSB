package com.aCompany.wms.dto;

import java.time.LocalDate;

public class StockReceiptDto {
    private Long productId;
    private Long locationId;
    private String batchNumber;
    private LocalDate expiryDate;
    private int quantity;
    private String reference;
    private String notes;


    // Getters
    public Long productId() {
        return productId;
    }

    public Long locationId() {
        return locationId;
    }

    public String batchNumber() {
        return batchNumber;
    }

    public int quantity() {
        return quantity;
    }

    public String reference() {
        return reference;
    }

    public String notes() {
        return notes;
    }

    public LocalDate expiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    // Setters if needed
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String referenceNumber() {
        return reference;
    }
}