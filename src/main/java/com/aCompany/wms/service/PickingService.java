package com.aCompany.wms.service;

import com.aCompany.wms.model.Invoice;
import com.aCompany.wms.model.StockTransaction;
import com.aCompany.wms.repository.InvoiceRepository;
import com.aCompany.wms.repository.StockTransactionRepository;
import com.aCompany.wms.util.SecurityUtil;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PickingService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private StockTransactionRepository transactionRepo;


    public void pickInvoice(Invoice invoice) {
        invoice.setPicked(true);
        invoiceRepository.save(invoice);

        StockTransaction tx = new StockTransaction();
        tx.setType("PICK");
        tx.setQuantity(1);
        tx.setTimestamp(LocalDateTime.now());
        tx.setPerformedBy(SecurityUtil.getCurrentUsername());

        transactionRepo.save(tx);
    }


    public List<Invoice> getPriorityOrders() {
        return invoiceRepository.findByPickedFalseOrderByOrder_CreatedAtAsc();
    }


}

