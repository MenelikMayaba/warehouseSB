package com.aCompany.warehouseSB.inventory;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class StockTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Item item;

    private int quantityChange;

    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public Item getItem() {
        return item;
    }

    public int getQuantityChange() {
        return quantityChange;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setQuantityChange(int quantityChange) {
        this.quantityChange = quantityChange;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}