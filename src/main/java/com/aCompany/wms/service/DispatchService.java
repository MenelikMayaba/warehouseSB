package com.aCompany.wms.service;

import com.aCompany.wms.model.Order;
import com.aCompany.wms.model.StockTransaction;
import com.aCompany.wms.repository.StockTransactionRepository;
import com.aCompany.wms.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DispatchService {
    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private StockTransactionRepository transactionRepo;

    public void dispatchOrder(Order order) {
        jmsTemplate.convertAndSend("dispatchQueue", order.getId());

        StockTransaction tx = new StockTransaction();
        tx.setType("DISPATCH");
        tx.setQuantity(order.getInvoices().size());
        tx.setTimestamp(LocalDateTime.now());
        tx.setPerformedBy(SecurityUtil.getCurrentUsername());

        transactionRepo.save(tx);

        order.setWorkflowStatus("DISPATCHED");
    }

}
