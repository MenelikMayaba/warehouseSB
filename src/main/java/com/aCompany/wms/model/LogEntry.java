package com.aCompany.wms.model;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_entries")
@Data
public class LogEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String level; // INFO, WARN, ERROR

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String action; // e.g., "LOGIN", "SCAN_INVOICE", "LOGOUT"

    @Column(columnDefinition = "TEXT")
    private String details;

    private String ipAddress;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
