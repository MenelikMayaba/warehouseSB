package com.aCompany.warehouseSB.invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByStatus(String status);

    long countByStatus(String status);
    // This will inherit all CRUD operations including findById
}