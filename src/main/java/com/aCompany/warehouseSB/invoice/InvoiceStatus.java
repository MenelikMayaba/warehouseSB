package com.aCompany.warehouseSB.invoice;

public enum InvoiceStatus {
    DRAFT,           // Initial state when invoice is created
    PICKING,         // Items are being picked
    PICKED,          // All items have been picked
    PACKING,         // Items are being packed
    PACKED,          // All items have been packed
    SHIPPED,         // Order has been shipped
    CANCELLED,       // Order was cancelled
    COMPLETED        // Order is completed
}
