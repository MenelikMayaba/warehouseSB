package com.aCompany.warehouseSB.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String itemName) {
        super("Not enough stock for item: " + itemName);
    }
}
