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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/receiving")
@PreAuthorize("hasRole('RECEIVING', 'ADMIN')")
public class ReceivingController {

    @Autowired
    private ReceivingService receivingService;

    @Autowired
    private StockLocationRepository locationRepository;

    @GetMapping
    public String receivingPage(Model model) {
        model.addAttribute("locations", locationRepository.findAll());
        return "/receivingUI/receivingDashboard";
    }

    @GetMapping("/scan-to-warehouse")
    public String showScanToWarehouseForm(Model model) {
        model.addAttribute("locations", locationRepository.findAll());
        return "/receivingUI/scan-to-warehouse";
    }

    @GetMapping("/put-away")
    public String showScanPutAway(Model model) {
        model.addAttribute("locations", locationRepository.findAll());
        return "/receivingUI/put-away";
    }


    @PostMapping("/scan")
    public String scanStock(
            @RequestParam String sku,
            @RequestParam int quantity,
            @RequestParam Long locationId,
            RedirectAttributes redirectAttributes) {

        try {
            StockLocation location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new RuntimeException("Location not found"));

            receivingService.receiveStock(sku, quantity, location);
            redirectAttributes.addFlashAttribute("success", "Successfully received " + quantity + " items of SKU: " + sku);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error receiving stock: " + e.getMessage());
            return "redirect:/receiving/scan-to-warehouse";
        }

        return "redirect:/scan-to-warehouse";
    }

    @PostMapping("/put-away")
    public String putAwayStock(
            @RequestParam String sku,
            @RequestParam int quantity,
            @RequestParam Long fromLocationId,
            @RequestParam Long toLocationId,
            RedirectAttributes redirectAttributes) {

        try {
            // Get the source and destination locations
            StockLocation fromLocation = locationRepository.findById(fromLocationId)
                    .orElseThrow(() -> new RuntimeException("Source location not found"));

            StockLocation toLocation = locationRepository.findById(toLocationId)
                    .orElseThrow(() -> new RuntimeException("Destination location not found"));

            // Call the service to handle the put-away logic
            receivingService.putAway(sku, quantity, toLocation);

            redirectAttributes.addFlashAttribute("success",
                    String.format("Successfully put away %d items of SKU: %s from %s to %s",
                            quantity, sku, fromLocation.getName(), toLocation.getName()));

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error during put-away: " + e.getMessage());
            return "redirect:/receiving/put-away";
        }

        return "redirect:/put-away";
    }

}
