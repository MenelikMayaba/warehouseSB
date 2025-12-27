package com.aCompany.wms.repository;

import com.aCompany.wms.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByPickedFalseAndStatus(String status);

    List<Invoice> findByPickedFalseOrderByOrder_CreatedAtAsc();
}

