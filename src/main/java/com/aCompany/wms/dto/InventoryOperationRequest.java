package com.aCompany.wms.dto;


import com.aCompany.wms.entity.Location;
import com.aCompany.wms.model.Product;
import com.aCompany.wms.entity.InventoryTransaction;

public class InventoryOperationRequest {
    private Product product;
    private Location fromLocation;
    private Location toLocation;
    private int quantity;
    private String referenceId;
    private String notes;
    private InventoryTransaction.TransactionType type;

    // constructor, getters, setters

    public Location getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(Location fromLocation) {
        this.fromLocation = fromLocation;
    }

    public Location getToLocation() {
        return toLocation;
    }

    public void setToLocation(Location toLocation) {
        this.toLocation = toLocation;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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

    public InventoryTransaction.TransactionType getType() {
        return type;
    }

    public void setType(InventoryTransaction.TransactionType type) {
        this.type = type;
    }


}


