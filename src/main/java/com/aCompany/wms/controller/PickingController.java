package com.aCompany.wms.controller;

import com.aCompany.wms.exceptions.InvoiceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import com.aCompany.wms.model.Invoice;
import com.aCompany.wms.service.PickingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String pickInvoice(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        pickingService.pickInvoiceById(id);
        return "redirect:/picking";
    }
    
    @ExceptionHandler(InvoiceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleInvoiceNotFound(InvoiceNotFoundException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/picking";
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleAllExceptions(Exception ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "An error occurred: " + ex.getMessage());
        return "redirect:/picking";
    }
}
