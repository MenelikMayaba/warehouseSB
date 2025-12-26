package com.aCompany.warehouseSB.scanner;

import com.aCompany.warehouseSB.invoice.InvoiceScanSummary;
import com.aCompany.warehouseSB.packing.DispatchInvoice;
import com.aCompany.warehouseSB.inventory.Item;
import com.aCompany.warehouseSB.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class CageScan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private DispatchInvoice dispatchInvoice;

    @ManyToOne
    private Item item;

    @ManyToOne
    private User scanner;

    private int quantityScanned;

    private LocalDateTime scannedAt;

    public Long getId() {
        return id;
    }

    public DispatchInvoice getDispatchInvoice() {
        return dispatchInvoice;
    }

    public void setDispatchInvoice(DispatchInvoice dispatchInvoice) {
        this.dispatchInvoice = dispatchInvoice;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public User getScanner() {
        return scanner;
    }

    public void setScanner(User scanner) {
        this.scanner = scanner;
    }

    public int getQuantityScanned() {
        return quantityScanned;
    }

    public void setQuantityScanned(int quantityScanned) {
        this.quantityScanned = quantityScanned;
    }

    public LocalDateTime getScannedAt() {
        return scannedAt;
    }

    public void setScannedAt(LocalDateTime scannedAt) {
        this.scannedAt = scannedAt;
    }

    public InvoiceScanSummary getCage() {
        Object cage = this.dispatchInvoice.getCage();
        if (cage instanceof InvoiceScanSummary) {
            return (InvoiceScanSummary) cage;
        }
        return new InvoiceScanSummary(
            null,  // invoiceId
            cage != null ? cage.toString() : "UNKNOWN",  // cageCode
            0,     // totalRequired
            0,     // totalScanned
            null,  // lastScannedBy
            null   // lastScannedAt
        );
    }
}
