package com.aCompany.wms.controller;

import org.springframework.ui.Model;
import com.aCompany.wms.model.Invoice;
import com.aCompany.wms.service.PickingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/picking")
public class PickingController {
    @Autowired
    private PickingService pickingService;

    @GetMapping
    public String viewPickingPage(Model model) {
        model.addAttribute("invoices", pickingService.getPriorityOrders());
        return "picking";
    }

    @PostMapping("/pick/{id}")
    public String pickInvoice(@PathVariable Long id) {
        Invoice invoice = pickingService.getPriorityOrders().stream()
                .filter(i -> i.getId().equals(id))
                .findFirst().orElseThrow();
        pickingService.pickInvoice(invoice);
        return "redirect:/picking";
    }
}
