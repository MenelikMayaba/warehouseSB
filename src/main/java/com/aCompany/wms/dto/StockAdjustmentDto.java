package com.aCompany.wms.dto;

public record StockAdjustmentDto(
        Long itemId,
        int adjustment,
        String referenceNumber,
        String reason,
        String notes
) {}