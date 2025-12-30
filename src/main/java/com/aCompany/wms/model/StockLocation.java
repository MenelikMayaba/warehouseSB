package com.aCompany.wms.model;

import jakarta.persistence.*;

@Entity
@Table(name = "stock_locations")
public class StockLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false)
    private String type; // e.g., HIGH_FREQUENCY, COLD_STORAGE, BULK_STORAGE
    
    private String description;
    private Integer capacity;
    private Integer currentOccupancy = 0;
    private boolean active = true;
    
    // Constructors
    public StockLocation() {
        // Default constructor for JPA
    }
    
    public StockLocation(String name, String type) {
        this.name = name;
        this.type = type;
    }
    
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
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Integer getCapacity() {
        return capacity;
    }
    
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    
    public Integer getCurrentOccupancy() {
        return currentOccupancy;
    }
    
    public void setCurrentOccupancy(Integer currentOccupancy) {
        this.currentOccupancy = currentOccupancy;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    // Business methods
    public boolean hasAvailableSpace() {
        return capacity == null || currentOccupancy < capacity;
    }
    
    public void increaseOccupancy(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (capacity != null && currentOccupancy + amount > capacity) {
            throw new IllegalStateException("Not enough capacity in location: " + name);
        }
        this.currentOccupancy += amount;
    }
    
    public void decreaseOccupancy(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (currentOccupancy - amount < 0) {
            throw new IllegalStateException("Cannot have negative occupancy");
        }
        this.currentOccupancy -= amount;
    }
    
    // Equals and HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockLocation that = (StockLocation) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "StockLocation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", capacity=" + capacity +
                ", currentOccupancy=" + currentOccupancy +
                ", active=" + active +
                '}';
    }
}
