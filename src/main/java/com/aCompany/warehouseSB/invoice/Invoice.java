package com.aCompany.warehouseSB.invoice;

import com.aCompany.warehouseSB.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items = new ArrayList<>();
    
    @ManyToOne
    private User picker;
    
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status = InvoiceStatus.DRAFT;
    
    @ManyToOne
    private User packer;
    
    private LocalDateTime packedAt;
    private String cage;  // Changed from Object to String

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
    }

    // Helper methods
    public void addItem(InvoiceItem item) {
        items.add(item);
        item.setInvoice(this);
    }

    public void removeItem(InvoiceItem item) {
        items.remove(item);
        item.setInvoice(null);
    }


    public boolean isPacked() {
        return items.stream().allMatch(item -> item.getItem().getQuantity() == item.getQuantity());
    }

    public boolean isPicked() {
        return items.stream().allMatch(item -> item.getItem().getQuantity() == item.getQuantity());
    }

    public void markPacked() {
        items.forEach(item -> item.getItem().setQuantity(item.getQuantity()));
    }

    public void setPickedBy(User picker) {
        this.picker = picker;
        this.status = InvoiceStatus.PICKED;
    }
    
    public InvoiceStatus getStatus() {
        return status;
    }
    
    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }
    
    public User getPicker() {
        return picker;
    }

    public void setPackedBy(User packer) {
        this.packer = packer;
    }

    public void setPackedAt(LocalDateTime now) {
        this.packedAt = now;
    }

    public User getPacker() {
        return packer;
    }

    public LocalDateTime getPackedAt() {
        return packedAt;
    }


    public String getOrderNumber() {
        return "INV-" + id;
    }

    public String getPriority() {
        return "Normal";
    }

    public Object getCage() {
        return cage;
    }


}