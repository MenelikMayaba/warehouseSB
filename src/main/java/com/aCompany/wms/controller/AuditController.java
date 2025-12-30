package com.aCompany.wms.controller;

import com.aCompany.wms.model.StockTransaction;
import com.aCompany.wms.repository.StockTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/audit")
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {

    @Autowired
    private StockTransactionRepository transactionRepo;

    @GetMapping
    public String viewAudit(
            @RequestParam(required = false) String user,
            @RequestParam(required = false) String type,
            Model model) {

        List<StockTransaction> transactions;

        if (user != null && !user.isEmpty()) {
            transactions = transactionRepo.findByPerformedBy(user);
        } else if (type != null && !type.isEmpty()) {
            transactions = transactionRepo.findByType(type);
        } else {
            transactions = transactionRepo.findAll();
        }

        model.addAttribute("transactions", transactions);
        return "audit";
    }
}
