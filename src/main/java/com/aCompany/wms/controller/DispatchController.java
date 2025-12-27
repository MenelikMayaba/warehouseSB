package com.aCompany.wms.controller;

import com.aCompany.wms.service.DispatchService;
import com.aCompany.wms.repository.OrderRepository;
import com.aCompany.wms.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dispatch")
public class DispatchController {
    @Autowired
    private DispatchService dispatchService;

    @PostMapping("/send/{id}")
    public String dispatchOrder(@PathVariable Long id, @Autowired OrderRepository orderRepo) {
        Order order = orderRepo.findById(id).orElseThrow();
        dispatchService.dispatchOrder(order);
        return "redirect:/dispatch";
    }
}

