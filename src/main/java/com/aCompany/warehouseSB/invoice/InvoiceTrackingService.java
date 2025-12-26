package com.aCompany.warehouseSB.invoice;

import com.aCompany.warehouseSB.scanner.CageScan;
import com.aCompany.warehouseSB.scanner.CageScanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class InvoiceTrackingService {

    private final CageScanRepository cageScanRepository;

    public InvoiceTrackingService(CageScanRepository cageScanRepository) {
        this.cageScanRepository = cageScanRepository;
    }

    public InvoiceScanSummary getInvoiceStatus(Long dispatchInvoiceId) {

        List<CageScan> scans =
                cageScanRepository.findByDispatchInvoiceId(dispatchInvoiceId);

        if (scans.isEmpty()) {
            throw new IllegalStateException("Invoice has not been scanned yet");
        }

        // Assume one cage per invoice (enforced by business rules)
        String cageCode = scans.get(0).getCage().getCageCode();

        int totalScanned = scans.stream()
                .mapToInt(CageScan::getQuantityScanned)
                .sum();

        int totalRequired = scans.get(0)
                .getDispatchInvoice()
                .getItems()
                .stream()
                .mapToInt(i -> i.getQuantity())
                .sum();

        // Find last scan event
        CageScan lastScan = scans.stream()
                .max(Comparator.comparing(CageScan::getScannedAt))
                .orElseThrow();

        String lastScannedBy = lastScan.getScanner().getUsername();
        LocalDateTime lastScannedAt = lastScan.getScannedAt();

        return new InvoiceScanSummary(
                dispatchInvoiceId,
                cageCode,
                totalRequired,
                totalScanned,
                lastScannedBy,
                lastScannedAt
        );
    }
}
