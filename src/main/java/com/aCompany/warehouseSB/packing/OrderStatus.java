package com.aCompany.warehouseSB.packing;

/**
 * Represents the status of an order in the packing process.
 */
public enum OrderStatus {
    /**
     * Order has been received and is ready to be packed.
     */
    READY_FOR_PACKING,
    
    /**
     * Order is currently being packed by a staff member.
     */
    PACKING_IN_PROGRESS,
    
    /**
     * Order has been packed and is ready for dispatch.
     */
    PACKED,
    
    /**
     * Order has been dispatched to the customer.
     */
    DISPATCHED,
    
    /**
     * Order has been cancelled and will not be processed.
     */
    CANCELLED
}
