package com.aCompany.warehouseSB.packing;

import com.aCompany.warehouseSB.invoice.Invoice;
import com.aCompany.warehouseSB.invoice.InvoiceScanSummary;
import com.aCompany.warehouseSB.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class DispatchInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Invoice originalInvoice;

    @ManyToOne
    private User packedBy;

    private LocalDateTime packedAt;

    @OneToMany(mappedBy = "dispatchInvoice", cascade = CascadeType.ALL)
    private List<DispatchInvoiceItem> items = new ArrayList<>();

    public DispatchInvoice(Invoice invoice) {
        this.originalInvoice = invoice;
        this.packedBy = invoice.getPacker();
        this.packedAt = invoice.getPackedAt();
    }

    public Long getId() {
        return id;
    }

    public Invoice getOriginalInvoice() {
        return originalInvoice;
    }

    public void setOriginalInvoice(Invoice originalInvoice) {
        this.originalInvoice = originalInvoice;
    }

    public User getPackedBy() {
        return packedBy;
    }

    public void setPackedBy(User packedBy) {
        this.packedBy = packedBy;
    }

    public LocalDateTime getPackedAt() {
        return packedAt;
    }

    public void setPackedAt(LocalDateTime packedAt) {
        this.packedAt = packedAt;
    }

    public List<DispatchInvoiceItem> getItems() {
        return items;
    }

    public Object getCage() {
        return this.originalInvoice.getCage();
    }
}
