package com.aCompany.wms.model;

import com.aCompany.wms.entity.Location;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stocks")
public class Stock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sku;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;
    
    private int quantity;
    
    @Column(name = "batch_number")
    private String batchNumber;
    
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;
    
    @Column(name = "manufacturing_date")
    private LocalDateTime manufacturingDate;
    
    private String status = "AVAILABLE";
    
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "scanned_at")
    private LocalDateTime scannedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Stock() {
    }
    
    public Stock(Product product, Location location, int quantity) {
        this.product = product;
        this.location = location;
        this.quantity = quantity;
    }
    
    public Stock(Product product, Location location, int quantity, String batchNumber,
                LocalDateTime manufacturingDate, LocalDateTime expiryDate) {
        this.product = product;
        this.location = location;
        this.quantity = quantity;
        this.batchNumber = batchNumber;
        this.manufacturingDate = manufacturingDate;
        this.expiryDate = expiryDate;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
        this.lastUpdated = LocalDateTime.now();
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
        this.lastUpdated = LocalDateTime.now();
    }

    public LocalDateTime getManufacturingDate() {
        return manufacturingDate;
    }

    public void setManufacturingDate(LocalDateTime manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
        this.lastUpdated = LocalDateTime.now();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.lastUpdated = LocalDateTime.now();
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    // Business methods
    public void addQuantity(int amount) {
        this.quantity += amount;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public void removeQuantity(int amount) {
        if (this.quantity >= amount) {
            this.quantity -= amount;
            this.lastUpdated = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Insufficient stock quantity");
        }
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setUpdatedAt(LocalDateTime now) {
        this.lastUpdated = now;
    }
}
