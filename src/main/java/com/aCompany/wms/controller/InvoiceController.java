package com.aCompany.wms.controller;

import com.aCompany.wms.model.Invoice;
import com.aCompany.wms.service.PickingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/invoices")
public class InvoiceController {
    @Autowired
    private PickingService pickingService;

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(
            @RequestParam Long orderId,
            @RequestParam(defaultValue = "PRIORITY") String status) {
        Invoice invoice = pickingService.createInvoice(orderId, status);
        return ResponseEntity.ok(invoice);
    }
}
