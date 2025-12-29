package com.aCompany.wms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StockPickDto(
        @NotNull(message = "Product ID is required")
        Long productId,
        
        @NotNull(message = "Location ID is required")
        Long locationId,
        
        @Positive(message = "Quantity must be greater than 0")
        int quantity,
        
        String referenceNumber,
        
        String notes
) {}
