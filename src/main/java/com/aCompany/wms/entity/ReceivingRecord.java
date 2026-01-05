package com.aCompany.wms.entity;

import com.aCompany.wms.model.Product;
import jakarta.persistence.*;

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
    private Location allocatedLocation;

    @Enumerated(EnumType.STRING)
    private ReceivingStatus status;

    @ManyToOne
    private Product product;

    private int putAwayQuantity = 0;


    public int getPutAwayQuantity() {
        return putAwayQuantity;
    }

    public void setPutAwayQuantity(int putAwayQuantity) {
        this.putAwayQuantity = putAwayQuantity;
    }


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



    public ReceivingStatus getStatus() {
        return status;
    }

    public void setStatus(ReceivingStatus status) {
        this.status = status;
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

    public Location getAllocatedLocation() {
        return allocatedLocation;
    }

    public void setAllocatedLocation(Location allocatedLocation) {
        this.allocatedLocation = allocatedLocation;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        if (product != null) {
            this.sku = product.getSku();
        }
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }


    public int getQuantity() {
        return quantity;
    }

    @Transient
    public int getRemainingQuantity() {
        return this.quantity - this.putAwayQuantity;
    }
}


