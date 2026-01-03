package com.aCompany.wms.exceptions;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Could not find product with id: " + id);
    }

    public ProductNotFoundException(String sku) {
        super("Could not find product with SKU: " + sku);
    }
}