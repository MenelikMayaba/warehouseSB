package com.aCompany.wms.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class StockLocation {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String type; // e.g., high-frequency, cold storage
}
