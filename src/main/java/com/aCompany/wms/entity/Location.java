package com.aCompany.wms.entity;

import com.aCompany.wms.model.Product;
import com.aCompany.wms.model.Stock;
import com.aCompany.wms.model.StockTransaction;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LocationType type;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    // Stock-specific fields
    @Column
    private Integer capacity;

    @Column(name = "current_occupancy")
    private Integer currentOccupancy = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "location")
    private Set<Product> products = new HashSet<>();

    @OneToMany(mappedBy = "allocatedLocation")
    private Set<ReceivingRecord> receivingRecords = new HashSet<>();

    @OneToMany(mappedBy = "location")
    private Set<Stock> stocks = new HashSet<>();

    @OneToMany(mappedBy = "location")
    private Set<StockTransaction> stockTransactions = new HashSet<>();


}