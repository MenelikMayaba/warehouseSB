package com.aCompany.wms.entity;

/**
 * Represents the status of received stock in the warehouse management system.
 * - RECEIVED: Stock has been received but not yet put away in the warehouse
 * - PUT_AWAY: Stock has been moved from receiving to the warehouse
 */
public enum ReceivingStatus {
    RECEIVED,   // Stock received but not yet put away
    PUT_AWAY    // Stock has been moved to warehouse
}
