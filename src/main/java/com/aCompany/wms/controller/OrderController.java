package com.aCompany.wms.controller;

import com.aCompany.wms.model.Order;
import com.aCompany.wms.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestParam(defaultValue = "PLANNING") String workflowStatus) {
        Order order = orderService.createOrder(workflowStatus);
        return ResponseEntity.ok(order);
    }
}
