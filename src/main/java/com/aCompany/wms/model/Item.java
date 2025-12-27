package com.aCompany.wms.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Item {
    @Id @GeneratedValue
    private Long id;
    private String sku;
    private String type;
    private int quantity;

    @ManyToOne
    private StockLocation location;

    public String getSku() {
        return sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int i) {
        this.quantity = i;
    }

    public void setLocation(StockLocation location) {
        this.location = location;
    }

    public String getName() {
        return this.type;
    }
}
