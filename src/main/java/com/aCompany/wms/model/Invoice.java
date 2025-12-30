package com.aCompany.wms.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Invoice {
    @Id
    @GeneratedValue
    private Long id;
    private String status; // PRIORITY, ACCURATE, UNUSED
    private boolean picked;

    @ManyToOne
    private Order order;

    public Long getId() {
        return id;
    }

    public void setPicked(boolean b) {
        this.picked = b;
    }

    public void setId(long l) {
        this.id = l;
    }

    public boolean isPicked() {
        return picked;
    }

    public void setStatus(String priority) {
        this.status = priority;
    }
}

