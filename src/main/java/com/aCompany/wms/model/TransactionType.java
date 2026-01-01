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
    PICKED,         // Stock has been picked for an order
    ADJUSTED,       // Stock quantity has been adjusted
    REMOVED,        // Stock has been removed from the system
    MOVED,          // Stock has been moved between locations
    DISPATCHED,     // Stock has been dispatched to a customer
    RETURNED,       // Stock has been returned by a customer
    PICK,           // Stock has been picked for an order (alternative to PICKED)
    PACK_RECOVERY,  // Stock has been recovered from a packed order
    PUT_AWAY,        // Stock has been put away to a storage location
    RECEIVED
}
