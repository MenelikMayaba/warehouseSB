package com.aCompany.wms.exceptions;

public class InvoiceNotFoundException extends RuntimeException {
    public InvoiceNotFoundException(Long missingId) {
        super("Invoice not found with id: " + missingId);
    }
}
