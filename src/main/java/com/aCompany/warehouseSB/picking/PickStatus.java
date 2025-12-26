package com.aCompany.warehouseSB.picking;

public enum PickStatus {
    PENDING,        // When the pick is created but not yet started
    IN_PROGRESS,    // When a picker is working on this pick
    COMPLETED,      // When the pick has been successfully completed
    CANCELLED       // When the pick has been cancelled
}
