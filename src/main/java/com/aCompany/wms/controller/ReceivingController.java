package com.aCompany.wms.controller;

import org.springframework.ui.Model;
import com.aCompany.wms.model.StockLocation;
import com.aCompany.wms.repository.StockLocationRepository;
import com.aCompany.wms.service.ReceivingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/receiving")
@PreAuthorize("hasRole('RECEIVING')")
public class ReceivingController {

    @Autowired
    private ReceivingService receivingService;

    @Autowired
    private StockLocationRepository locationRepository;

    @GetMapping
    public String receivingPage(Model model) {
        model.addAttribute("locations", locationRepository.findAll());
        return "receiving";
    }

    @PostMapping("/scan")
    public String scanStock(
            @RequestParam String sku,
            @RequestParam int quantity,
            @RequestParam Long locationId) {

        StockLocation location = locationRepository.findById(locationId)
                .orElseThrow();

        receivingService.receiveStock(sku, quantity, location);
        return "redirect:/receiving";
    }
}
