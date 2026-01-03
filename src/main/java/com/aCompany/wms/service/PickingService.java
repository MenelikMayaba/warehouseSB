package com.aCompany.wms.service;

import com.aCompany.wms.exceptions.InvoiceNotFoundException;
import com.aCompany.wms.model.Invoice;
import com.aCompany.wms.model.Order;
import com.aCompany.wms.model.StockTransaction;
import com.aCompany.wms.model.TransactionType;
import com.aCompany.wms.repository.InvoiceRepository;
import com.aCompany.wms.repository.OrderRepository;
import com.aCompany.wms.repository.StockTransactionRepository;
import com.aCompany.wms.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PickingService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StockTransactionRepository transactionRepo;


    public void pickInvoice(Invoice invoice) {
        invoice.setPicked(true);
        invoiceRepository.save(invoice);

        StockTransaction tx = new StockTransaction();
        tx.setType(TransactionType.valueOf("PICK"));
        tx.setQuantity(1);
        tx.setTimestamp(LocalDateTime.now());
        tx.setPerformedBy(SecurityUtil.getCurrentUsername());

        transactionRepo.save(tx);
    }


    public List<Invoice> getPriorityOrders() {
        return invoiceRepository.findByPickedFalseOrderByOrder_CreatedAtAsc();
    }


    public void pickInvoiceById(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException(invoiceId));

        pickInvoice(invoice);
    }

    public Invoice createInvoice(Long orderId, String status) {
        // Validate status
        if (!List.of("PRIORITY", "ACCURATE", "UNUSED").contains(status)) {
            throw new IllegalArgumentException("Invalid status. Must be one of: PRIORITY, ACCURATE, UNUSED");
        }

        // Find the order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // Check if invoice already exists for this order
        if (invoiceRepository.existsByOrderId(orderId)) {
            throw new IllegalStateException("An invoice already exists for order id: " + orderId);
        }

        // Create and save the invoice
        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setStatus(status);
        invoice.setPicked(false);

        return invoiceRepository.save(invoice);
    }
}

