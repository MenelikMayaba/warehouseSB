package com.aCompany.wms.dto;

public class PickingTaskDTO {
    private Long invoiceId;
    private String orderStatus;
    private String sku;
    private int quantity;

    public PickingTaskDTO(Long invoiceId, String orderStatus, String sku, int quantity) {
        this.invoiceId = invoiceId;
        this.orderStatus = orderStatus;
        this.sku = sku;
        this.quantity = quantity;
    }

    // getters and setters
    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

