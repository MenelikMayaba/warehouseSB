package com.aCompany.wms.service;

import com.aCompany.wms.dto.InventoryOperationRequest;
import com.aCompany.wms.dto.ReceiveStockDto;
import com.aCompany.wms.entity.*;
import com.aCompany.wms.exceptions.InsufficientStockException;
import com.aCompany.wms.exceptions.ResourceNotFoundException;
import com.aCompany.wms.model.Product;
import com.aCompany.wms.repository.InventoryRepository;
import com.aCompany.wms.repository.InventoryTransactionRepository;
import com.aCompany.wms.repository.LocationRepository;
import com.aCompany.wms.repository.ProductRepository;
import com.aCompany.wms.util.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final LocationRepository locationRepository;

    @Transactional
    public Inventory receiveStock(ReceiveStockDto request) throws ResourceNotFoundException {
        // Validate request
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        // Get product and location
        Product product = request.getProduct();
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }

        Location toLocation = request.getToLocation();
        if (toLocation == null) {
            throw new IllegalArgumentException("To Location cannot be null");
        }

        // Create inventory operation request
        InventoryOperationRequest operationRequest = new InventoryOperationRequest();
        operationRequest.setProduct(product);
        operationRequest.setToLocation(toLocation);
        operationRequest.setQuantity(request.getQuantity());
        operationRequest.setReferenceId(request.getReferenceId());
        operationRequest.setNotes(request.getNotes());
        operationRequest.setType(InventoryTransaction.TransactionType.RECEIVE);

        return updateInventory(operationRequest);
    }


    @Transactional
    public Inventory putAwayStock(Long productId, Long fromLocationId, Long toLocationId, int quantity, String referenceId) throws ResourceNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        Location fromLocation = locationRepository.findById(fromLocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Source location not found: " + fromLocationId));
        Location toLocation = locationRepository.findById(toLocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Destination location not found: " + toLocationId));

        InventoryOperationRequest request = new InventoryOperationRequest();
        request.setProduct(product);
        request.setFromLocation(fromLocation);
        request.setToLocation(toLocation);
        request.setQuantity(quantity);
        request.setReferenceId(referenceId);
        request.setNotes("Put away from " + fromLocation.getCode() + " to " + toLocation.getCode());
        request.setType(InventoryTransaction.TransactionType.PUT_AWAY);

        return updateInventory(request);
    }


    @Transactional
    public Inventory adjustStock(Long productId, Long locationId, int quantity, String reason, String referenceId) throws ResourceNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + locationId));

        Inventory existingInventory = inventoryRepository.findByProductAndLocation(product, location)
                .orElseGet(() -> {
                    Inventory inv = new Inventory();
                    inv.setProduct(product);
                    inv.setLocation(location);
                    return inv;
                });

        int quantityDelta = quantity - existingInventory.getQuantity();

        InventoryOperationRequest request = new InventoryOperationRequest();
        request.setProduct(product);
        request.setFromLocation(location);  // same for source
        request.setToLocation(location);    // same for destination
        request.setQuantity(quantityDelta); // delta instead of full quantity
        request.setReferenceId(referenceId);
        request.setNotes("Stock adjustment. " + (reason != null ? "Reason: " + reason : ""));
        request.setType(InventoryTransaction.TransactionType.ADJUST);

        return updateInventory(request);
    }


    @Transactional
    public Inventory transferStock(Long productId, Long fromLocationId, Long toLocationId, int quantity, String referenceId, String notes) throws ResourceNotFoundException {
        if (fromLocationId.equals(toLocationId)) {
            throw new IllegalArgumentException("Source and destination locations cannot be the same");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        Location fromLocation = locationRepository.findById(fromLocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Source location not found: " + fromLocationId));
        Location toLocation = locationRepository.findById(toLocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Destination location not found: " + toLocationId));

        InventoryOperationRequest request = new InventoryOperationRequest();
        request.setProduct(product);
        request.setFromLocation(fromLocation);
        request.setToLocation(toLocation);
        request.setQuantity(quantity);
        request.setReferenceId(referenceId);
        request.setNotes(notes);
        request.setType(InventoryTransaction.TransactionType.TRANSFER);

        return updateInventory(request);
    }


    private void recordTransaction(InventoryOperationRequest request) {
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setType(request.getType());
        transaction.setProduct(request.getProduct());
        transaction.setFromLocation(request.getFromLocation());
        transaction.setToLocation(request.getToLocation());
        transaction.setQuantity(request.getQuantity());
        transaction.setReferenceId(request.getReferenceId());
        transaction.setNotes(request.getNotes());
        transaction.setCreatedBy(SecurityUtil.getCurrentUsername());
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);
    }


    // Additional helper methods
    public int getAvailableQuantity(Long productId, Long locationId) {
        return inventoryRepository.findByProductIdAndLocationId(productId, locationId)
                .map(Inventory::getQuantity)
                .orElse(0);
    }

    public List<Inventory> getInventoryByProduct(Product product) {
        return inventoryRepository.findByProduct(product);
    }

    public List<Inventory> getInventoryByLocation(Location location) {
        return inventoryRepository.findByLocation(location);
    }

    public List<Inventory> getInventoryByStatus(Inventory.InventoryStatus status) {
        return inventoryRepository.findByStatus(status);
    }

    private Inventory updateInventory(InventoryOperationRequest request) throws InsufficientStockException {
        Inventory sourceInventory = null;
        if (request.getFromLocation() != null) {
            sourceInventory = inventoryRepository
                    .findByProductAndLocation(request.getProduct(), request.getFromLocation())
                    .orElseThrow(() -> new InsufficientStockException("No inventory at source location"));

            if (sourceInventory.getQuantity() < request.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock. Available: " + sourceInventory.getQuantity() +
                                ", Requested: " + request.getQuantity()
                );
            }

            sourceInventory.setQuantity(sourceInventory.getQuantity() - request.getQuantity());
            if (sourceInventory.getQuantity() == 0) inventoryRepository.delete(sourceInventory);
            else inventoryRepository.save(sourceInventory);
        }

        Inventory destInventory = null;
        if (request.getToLocation() != null) {
            destInventory = inventoryRepository.findByProductAndLocation(request.getProduct(), request.getToLocation())
                    .orElseGet(() -> {
                        Inventory inv = new Inventory();
                        inv.setProduct(request.getProduct());
                        inv.setLocation(request.getToLocation());
                        inv.setStatus(Inventory.InventoryStatus.STORED);
                        return inv;
                    });

            destInventory.setQuantity(destInventory.getQuantity() + request.getQuantity());
            destInventory.setStatus(Inventory.InventoryStatus.STORED);
            destInventory = inventoryRepository.save(destInventory);
        }

        // Record transaction
        recordTransaction(request);

        // Return destination inventory if exists, otherwise source inventory
        return destInventory != null ? destInventory : sourceInventory;
    }


}
