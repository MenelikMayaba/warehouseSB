package com.aCompany.wms.model;

import com.aCompany.wms.entity.Location;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "SKU is required")
    @Column(nullable = false, unique = true)
    private String sku;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;
    
    private String description;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be positive")
    @Column(nullable = false)
    private Double price;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(nullable = false)
    private Integer quantityInStock;

    @ManyToOne
    private Location location;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @Column(nullable = false)
    private String status;

    @ManyToOne
    private Product product;

    @Column(nullable = false)
    private String batchNumber;

    @Column(name = "expiry_date", nullable = true, columnDefinition = "TIMESTAMP NULL")
    private LocalDateTime expiryDate;

    @Column(name = "reorder_level", nullable = false)
    private Integer reorderLevel = 10; // Default reorder level

    @Column(name = "storage_type", nullable = false)
    private String storageType = "GENERAL"; // Default storage type


    // Additional fields can be added as needed
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(Integer quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", quantityInStock=" + quantityInStock +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id != null && id.equals(product.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getSku() {
        return sku;
    }

    public int getQuantity() {
        return quantityInStock;
    }

    public void setQuantity(int i) {
        this.quantityInStock = i;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setLastUpdated(LocalDateTime now) {
        this.lastUpdated = now;
    }

    public void setUpdatedAt(LocalDateTime now) {
        this.lastUpdated = now;
    }

    public String getStatus() {
        return status;
    }
    
    public void setStatus(String outOfStock) {
        this.status = outOfStock;
    }

    public void setProduct(Product productNotFound) {
        this.product = productNotFound;
    }

    public String getBatchNumber() {
        return batchNumber;
    }
    
    public void setBatchNumber(String s) {
        this.batchNumber = s;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    @PrePersist
    @PreUpdate
    public void updateTimestamps() {
        this.lastUpdated = LocalDateTime.now();
        if (this.reorderLevel == null) {
            this.reorderLevel = 10; // Default reorder level
        }
        if (this.storageType == null) {
            this.storageType = "GENERAL"; // Default storage type
        }
    }
}
