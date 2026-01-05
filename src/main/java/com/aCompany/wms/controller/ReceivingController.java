package com.aCompany.wms.controller;

import com.aCompany.wms.dto.ReceivingForm;
import com.aCompany.wms.entity.Location;
import com.aCompany.wms.entity.LocationType;
import com.aCompany.wms.entity.ReceivingRecord;
import com.aCompany.wms.entity.ReceivingStatus;
import com.aCompany.wms.repository.LocationRepository;
import com.aCompany.wms.repository.ReceivingRepository;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import com.aCompany.wms.service.ReceivingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
        // Get all locations except RECEIVING for the dropdown
        List<Location> locations = locationRepository.findByTypeNot(LocationType.RECEIVING);
        model.addAttribute("locations", locations);
        model.addAttribute("receivingForm", new ReceivingForm());
        return "receivingUI/scan-to-warehouse";
    }

    @PostMapping("/scan-to-warehouse")
    public String receiveStock(@ModelAttribute ReceivingForm receivingForm,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            // Get the current user
            String username = authentication.getName();

            // Call the service to handle the receiving
            receivingService.receiveStock(
                    receivingForm.getSku(),
                    receivingForm.getQuantity(),
                    receivingForm.getLocationId(),
                    username
            );

            redirectAttributes.addFlashAttribute("success", "Stock received successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error receiving stock: " + e.getMessage());
        }

        return "redirect:/receiving/scan-to-warehouse";
    }

    @GetMapping("/put-away")
    public String showPutAwayPage(Model model) {
        // Get all receiving records that need to be put away (status = RECEIVED and remainingQuantity > 0)
        List<ReceivingRecord> receivingRecords = receivingRepository.findByStatusAndRemainingQuantityGreaterThan(
                ReceivingStatus.RECEIVED, 0);

        // Get all warehouse locations (excluding RECEIVING)
        List<Location> warehouseLocations = locationRepository.findByTypeNot(LocationType.RECEIVING);

        model.addAttribute("receivingRecords", receivingRecords);
        model.addAttribute("warehouseLocations", warehouseLocations);

        return "receivingUI/put-away";
    }

    @PostMapping("/receiving/put-away")
    public String putAwayStock(@RequestParam Long receivingRecordId,
                               @RequestParam Long locationId,
                               @RequestParam int quantity,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();

            // Call the service to handle the put-away
            receivingService.putAway(receivingRecordId, locationId, quantity, username);

            redirectAttributes.addFlashAttribute("success", "Stock put away successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error putting away stock: " + e.getMessage());
        }

        return "redirect:/receiving/put-away";
    }


    @PostMapping("/scan")
    public String scanStock(
            @RequestParam String sku,
            @RequestParam int quantity,
            @RequestParam Long locationId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            Location location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new RuntimeException("Location not found"));

            receivingService.receiveStock(sku, quantity, locationId, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Successfully received " + quantity + " items of SKU: " + sku);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error receiving stock: " + e.getMessage());
            return "redirect:/receiving/scan-to-warehouse";
        }

        return "redirect:/scan-to-warehouse";
    }

    @PostMapping("/put-away")
    public String putAwayStock(
            @RequestParam Long receivingRecordId,
            @RequestParam String sku,
            @RequestParam int quantity,
            @RequestParam Long toLocationId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            // Get the destination location (warehouse location)
            Location toLocation = locationRepository.findById(toLocationId)
                    .orElseThrow(() -> new RuntimeException("Destination location not found"));

            // Get the receiving location (source)
            Location fromLocation = locationRepository.findByCode("RECEIVING")
                    .orElseThrow(() -> new RuntimeException("Receiving location not found"));

            // Call the service to handle the put-away logic
            String username = authentication.getName();
            receivingService.putAway(receivingRecordId, toLocationId, quantity, username);

            redirectAttributes.addFlashAttribute("success",
                    String.format("Successfully put away %d items of SKU: %s to %s",
                            quantity, sku, toLocation.getName()));

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error during put-away: " + e.getMessage());
            return "redirect:/receiving/put-away";
        }

        return "redirect:/receiving/put-away";
    }

}
