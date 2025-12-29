package com.aCompany.wms.entity;

import com.aCompany.wms.model.StockLocation;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class ReceivingRecord {

    @Id
    @GeneratedValue
    private Long id;

    private String sku;
    private int quantity;

    private String source; // supplier, return, transfer
    private LocalDateTime receivedAt;
    private String receivedBy;

    @ManyToOne
    private StockLocation allocatedLocation;

    // getters and setters

    public String getSku() {
        return sku;
    }


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }



    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }

    public StockLocation getAllocatedLocation() {
        return allocatedLocation;
    }

    public void setAllocatedLocation(StockLocation allocatedLocation) {
        this.allocatedLocation = allocatedLocation;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
