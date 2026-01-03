package com.aCompany.wms.service;

import com.aCompany.wms.model.Order;
import com.aCompany.wms.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public Order createOrder(String workflowStatus) {
        Order order = new Order();
        order.setWorkflowStatus(workflowStatus);
        order.setCreatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }
    
    public long countByWorkflowStatus(String status) {
        return orderRepository.countByWorkflowStatus(status);
    }

    public long countByStatus(String status) {
        return orderRepository.countByWorkflowStatus(status);
    }
}
