package com.aCompany.wms.model;

/**
 * Represents the type of stock transaction in the warehouse management system.
 * - PICKED: Stock has been picked for an order
 * - ADJUSTED: Stock quantity has been adjusted
 * - REMOVED: Stock has been removed from the system
 * - MOVED: Stock has been moved between locations
 * - DISPATCHED: Stock has been dispatched to a customer
 * - RETURNED: Stock has been returned by a customer
 * - PICK: Stock has been picked for an order (alternative to PICKED)
 * - PACK_RECOVERY: Stock has been recovered from a packed order
 */
public enum TransactionType {
    RECEIVED,       // When items are received from suppliers
    PICKED,         // When items are picked for orders
    PACKED,         // When items are packed
    SHIPPED,        // When orders are shipped
    ADJUSTED,       // When inventory is manually adjusted
    TRANSFERRED,    // When items are moved between locations
    RETURNED,       // When items are returned
    DAMAGED,        // When items are marked as damaged
    LOST,           // When items are marked as lost
    FOUND,          // When lost items are found
    CYCLE_COUNT,    // For inventory cycle counts
    PRODUCT_UPDATE, // When product details are updated
    ORDER_UPDATE,   // When order status changes
    SYSTEM,         // For system-generated events
    MOVED,
    PUT_AWAY,
    PACK_RECOVERY
}
