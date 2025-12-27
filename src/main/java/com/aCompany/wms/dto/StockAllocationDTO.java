package com.aCompany.wms.dto;

public class StockAllocationDTO {
    private String sku;
    private String currentLocation;
    private String suggestedLocation;
    private int quantity;

    public StockAllocationDTO(String sku, String currentLocation, String suggestedLocation, int quantity) {
        this.sku = sku;
        this.currentLocation = currentLocation;
        this.suggestedLocation = suggestedLocation;
        this.quantity = quantity;
    }

    // getters and setters
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
    public String getSuggestedLocation() { return suggestedLocation; }
    public void setSuggestedLocation(String suggestedLocation) { this.suggestedLocation = suggestedLocation; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

