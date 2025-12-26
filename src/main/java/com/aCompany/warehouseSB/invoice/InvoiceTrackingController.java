package com.aCompany.warehouseSB.invoice;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/invoices")
public class InvoiceTrackingController {

    private final InvoiceTrackingService trackingService;

    public InvoiceTrackingController(InvoiceTrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @GetMapping("/{dispatchInvoiceId}/scan-status")
    @PreAuthorize("hasAuthority('SCANNER_ADMIN') or hasAuthority('WAREHOUSE_MANAGER')")
    public InvoiceScanSummary searchInvoice(
            @PathVariable Long dispatchInvoiceId
    ) {
        return trackingService.getInvoiceStatus(dispatchInvoiceId);
    }
}
