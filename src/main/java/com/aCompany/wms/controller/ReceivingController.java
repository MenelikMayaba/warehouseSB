package com.aCompany.wms.controller;

import com.aCompany.wms.entity.Location;
import com.aCompany.wms.entity.ReceivingRecord;
import com.aCompany.wms.entity.ReceivingStatus;
import com.aCompany.wms.repository.LocationRepository;
import com.aCompany.wms.repository.ReceivingRepository;
import org.springframework.ui.Model;
import com.aCompany.wms.service.ReceivingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/receiving")
@PreAuthorize("hasRole('RECEIVING', 'ADMIN')")
public class ReceivingController {

    @Autowired
    private ReceivingService receivingService;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ReceivingRepository receivingRepository;

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

        // Get all receiving records with their products
        List<ReceivingRecord> receivingRecords = receivingRepository.findByStatus(ReceivingStatus.RECEIVED);

        // Log the results for debugging
        System.out.println("=== Receiving Records ===");
        System.out.println("Found " + receivingRecords.size() + " receiving records");
        receivingRecords.forEach(record -> {
            System.out.println("Record ID: " + record.getId() +
                    ", Product: " + (record.getProduct() != null ?
                    record.getProduct().getName() + " (ID: " + record.getProduct().getId() + ")" : "null") +
                    ", Qty: " + record.getQuantity() +
                    ", Status: " + record.getStatus());
        });

        model.addAttribute("receivingRecords", receivingRecords);
        return "/receivingUI/put-away";

    }


    @PostMapping("/scan")
    public String scanStock(
            @RequestParam String sku,
            @RequestParam int quantity,
            @RequestParam Long locationId,
            RedirectAttributes redirectAttributes) {

        try {
            Location location = locationRepository.findById(locationId)
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
            Location fromLocation = locationRepository.findById(fromLocationId)
                    .orElseThrow(() -> new RuntimeException("Source location not found"));

            Location toLocation = locationRepository.findById(toLocationId)
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
