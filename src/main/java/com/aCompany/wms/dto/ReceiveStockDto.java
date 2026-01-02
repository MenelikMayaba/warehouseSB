package com.aCompany.wms.dto;

import com.aCompany.wms.entity.Location;
import com.aCompany.wms.model.Product;

public class ReceiveStockDto {
    private Product product;
    private Location toLocation;
    private Long productId;
    private Long locationId;
    private int quantity;
    private String referenceId;
    private String notes;

    // Getters and setters
    // Constructor(s)


    public ReceiveStockDto(Long productId, Long locationId, int quantity, String referenceId, String notes) {
        this.productId = productId;
        this.locationId = locationId;
        this.quantity = quantity;
        this.referenceId = referenceId;
        this.notes = notes;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Location getToLocation() {
        return toLocation;
    }

    public void setToLocation(Location toLocation) {
        this.toLocation = toLocation;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
