package com.aCompany.wms.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sku;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    private String description;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(nullable = false)
    private Integer quantityInStock;

    @ManyToOne
    private StockLocation location;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @Column(nullable = false)
    private String status;

    @ManyToOne
    private Product product;

    @Column(nullable = false)
    private String batchNumber;

    @Column(nullable = false)
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

    public void setLocation(StockLocation location) {
        this.location = location;
    }

    public void setLastUpdated(LocalDateTime now) {
        this.lastUpdated = now;
    }

    public void setUpdatedAt(LocalDateTime now) {
        this.lastUpdated = now;
    }

    public void setStatus(String outOfStock) {
        this.status = outOfStock;
    }

    public void setProduct(Product productNotFound) {
        this.product = productNotFound;
    }

    public void setBatchNumber(String s) {
        this.batchNumber = s;
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
}
