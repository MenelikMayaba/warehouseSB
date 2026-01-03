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

import java.util.List;

@Controller
@RequestMapping("/picker")
public class PickingController {
    private static final int DEFAULT_PAGE_SIZE = 10;

    @Autowired
    private PickingService pickingService;

    @GetMapping
    public String viewPickingPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        // Get the total number of tasks
        List<Invoice> allTasks = pickingService.getPriorityOrders();
        int totalTasks = allTasks.size();
        int totalPages = (int) Math.ceil((double) totalTasks / size);

        // Apply pagination
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalTasks);
        List<Invoice> pagedTasks = allTasks.subList(fromIndex, toIndex);

        // Add attributes to the model
        model.addAttribute("pickingTasks", pagedTasks);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalTasks", totalTasks);

        return "/pickerUI/pickerDashboard";
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
