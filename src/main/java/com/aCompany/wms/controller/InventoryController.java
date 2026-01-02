package com.aCompany.wms.controller;

import com.aCompany.wms.dto.ReceiveStockDto;
import com.aCompany.wms.entity.Inventory;
import com.aCompany.wms.entity.Inventory.InventoryStatus;
import com.aCompany.wms.entity.Location;
import com.aCompany.wms.model.Product;
import com.aCompany.wms.exceptions.InsufficientStockException;
import com.aCompany.wms.exceptions.ResourceNotFoundException;
import com.aCompany.wms.service.InventoryService;
import com.aCompany.wms.service.LocationService;
import com.aCompany.wms.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/receive")
    public ResponseEntity<Inventory> receiveStock(@RequestBody ReceiveStockDto receiveDto) {
        try {
            Inventory result = inventoryService.receiveStock(receiveDto);
            return ResponseEntity.ok(result);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<Inventory> transferStock(
            @RequestParam Long productId,
            @RequestParam Long fromLocationId,
            @RequestParam Long toLocationId,
            @RequestParam int quantity,
            @RequestParam(required = false) String referenceId,
            @RequestParam(required = false) String notes) throws ResourceNotFoundException {
        Inventory inventory = inventoryService.transferStock(productId, fromLocationId, toLocationId, quantity, referenceId, notes);
        return ResponseEntity.ok(inventory);
    }

    @PostMapping("/adjust")
    public ResponseEntity<Inventory> adjustStock(
            @RequestParam Long productId,
            @RequestParam Long locationId,
            @RequestParam int quantity,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) String referenceId) throws ResourceNotFoundException {
        Inventory inventory = inventoryService.adjustStock(productId, locationId, quantity, reason, referenceId);
        return ResponseEntity.ok(inventory);
    }
}