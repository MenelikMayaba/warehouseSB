package com.aCompany.wms.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;  // Add this import

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"order\"")  // Add this line with quoted table name
public class Order {
    @Id
    @GeneratedValue
    private Long id;
    private String workflowStatus; // PLANNING, PICKING, PACKING, DISPATCHED
    private LocalDateTime createdAt;

    @OneToMany(mappedBy="order")
    private List<Invoice> invoices;

    public void setWorkflowStatus(String dispatched) {
        this.workflowStatus = dispatched;
    }

    public Long getId() {
        return id;
    }

    public List<Invoice> getInvoices() {
        if (invoices == null) {
            invoices = new ArrayList<>();
        }
        return invoices;
    }

    public void setCreatedAt(LocalDateTime now) {
        this.createdAt = now;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }
}