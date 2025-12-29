package com.aCompany.wms.service;

import com.aCompany.wms.dto.StockAdjustmentDto;
import com.aCompany.wms.dto.StockReceiptDto;
import com.aCompany.wms.exeptions.*;
import com.aCompany.wms.model.Item;
import com.aCompany.wms.model.StockLocation;
import com.aCompany.wms.model.StockTransaction;
import com.aCompany.wms.dto.StockPickDto;
import com.aCompany.wms.repository.*;
import com.aCompany.wms.util.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StockService {
    private final ItemRepository itemRepository;
    private final StockTransactionRepository transactionRepo;
    private final ProductRepository productRepository;
    private final StockLocationRepository locationRepository;
    private final UserRepository userRepository;

    @Autowired
    public StockService(ItemRepository itemRepository,
                        StockTransactionRepository transactionRepo,
                        ProductRepository productRepository,
                        StockLocationRepository locationRepository,
                        UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.transactionRepo = transactionRepo;
        this.productRepository = productRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('RECEIVER')")
    public Item receiveStock(StockReceiptDto receipt) {
        // Find or create stock entry
        Item item = itemRepository.findByProductIdAndLocationIdAndBatchNumber(
                receipt.productId(),
                receipt.locationId(),
                receipt.batchNumber()
        ).orElseGet(() -> {
            Item newItem = new Item();
            try {
                newItem.setProduct(productRepository.findById(receipt.productId())
                        .orElseThrow(() -> new ProductNotFoundException("Product not found")));
            } catch (ProductNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                newItem.setLocation(locationRepository.findById(receipt.locationId())
                        .orElseThrow(() -> new LocationNotFoundException("Location not found")));
            } catch (LocationNotFoundException e) {
                throw new RuntimeException(e);
            }
            newItem.setBatchNumber(receipt.batchNumber());
            newItem.setExpiryDate(receipt.expiryDate().atStartOfDay());
            return newItem;
        });

        // Update quantity
        item.setQuantity(item.getQuantity() + receipt.quantity());
        item.setLastUpdated(LocalDateTime.now());
        itemRepository.save(item);

        // Log transaction
        logTransaction(item, "RECEIVED", receipt.quantity(), receipt.referenceNumber(), receipt.notes());

        return item;
    }


    @PreAuthorize("hasRole('PICKER')")
    @Transactional
    public Item pickStock(StockPickDto pick) throws StockNotFoundException, InsufficientStockException {
        // Find all available stock items for the product and location
        List<Item> availableItems = itemRepository.findAvailableStock(
                pick.productId(),
                pick.locationId()
        );

        if (availableItems.isEmpty()) {
            throw new StockNotFoundException("No available stock found for the specified product and location");
        }

        // Find the first item with sufficient quantity
        Item selectedItem = availableItems.stream()
                .filter(item -> item.getQuantity() >= pick.quantity())
                .findFirst()
                .orElseThrow(() -> {
                    int totalAvailable = availableItems.stream()
                            .mapToInt(Item::getQuantity)
                            .sum();
                    return new InsufficientStockException(
                            String.format("Insufficient stock. Total available: %d, Requested: %d",
                                    totalAvailable, pick.quantity())
                    );
                });

        // Update quantity
        int newQuantity = selectedItem.getQuantity() - pick.quantity();
        selectedItem.setQuantity(newQuantity);
        selectedItem.setUpdatedAt(LocalDateTime.now());
        
        // If quantity reaches zero, you might want to archive or delete the item
        if (newQuantity == 0) {
            selectedItem.setStatus("OUT_OF_STOCK");
        }
        
        itemRepository.save(selectedItem);

        // Log transaction
        logTransaction(selectedItem, "PICKED", -pick.quantity(), 
                pick.referenceNumber(), pick.notes());

        return selectedItem;
    }

    @PreAuthorize("hasAnyRole('RECEIVER', 'PICKER', 'ADMIN')")
    public List<Item> getStockByProduct(Long productId) {
        return itemRepository.findByProductId(productId);
    }

    @PreAuthorize("hasAnyRole('RECEIVER', 'PICKER', 'ADMIN')")
    public List<Item> getStockByLocation(Long locationId) {
        return itemRepository.findByLocationId(locationId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void adjustStock(StockAdjustmentDto adjustment) throws ItemNotFoundException {
        // Find the item
        Item item = itemRepository.findById(adjustment.itemId())
                .orElseThrow(() -> new ItemNotFoundException("Item not found with ID: " + adjustment.itemId()));

        // Calculate new quantity
        int newQuantity = item.getQuantity() + adjustment.adjustment();
        if (newQuantity < 0) {
            throw new InsufficientStockException("Cannot adjust stock below zero");
        }

        // Update item quantity
        item.setQuantity(newQuantity);
        item.setLastUpdated(LocalDateTime.now());
        itemRepository.save(item);

        // Log the adjustment
        logTransaction(
                item,
                "ADJUST",
                adjustment.adjustment(),
                adjustment.referenceNumber(),
                "Reason: " + adjustment.reason() + ". " + (adjustment.notes() != null ? adjustment.notes() : "")
        );
    }

    public void putAway(Item item, StockLocation location) {
        item.setLocation(location);
        itemRepository.save(item);

        StockTransaction tx = new StockTransaction();
        tx.setType("PUTAWAY");
        tx.setQuantity(item.getQuantity());
        tx.setItem(item);
        tx.setTimestamp(LocalDateTime.now());
        tx.setPerformedBy(SecurityUtil.getCurrentUsername());

        transactionRepo.save(tx);
    }

    private void logTransaction(Item item, String type, int quantity, String reference, String notes) {
        StockTransaction tx = new StockTransaction();
        tx.setType(type);
        tx.setItem(item);
        tx.setQuantity(quantity);
        tx.setReferenceNumber(reference);
        tx.setNotes(notes);
        tx.setTimestamp(LocalDateTime.now());
        tx.setPerformedBy(SecurityUtil.getCurrentUsername());
        transactionRepo.save(tx);
    }

    // Existing methods with improvements
    public List<Item> getLowStockItems() {
        int lowStockThreshold = 10; // Default threshold of 10 items
        return itemRepository.getLowStockItems(lowStockThreshold);
    }

    public List<Item> searchItems(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return itemRepository.findAll();
        }

        String searchKey = searchTerm.trim().toLowerCase();
        return itemRepository.searchByNameOrSku(searchKey);
    }

    public List<Item> getOptimizedStorage(String highDemand) {
        // Get all items from the repository
        List<Item> allItems = itemRepository.findAll();

        // Sort items based on optimization criteria
        return allItems.stream()
                .sorted((item1, item2) -> {
                    // Example optimization: Sort by demand if highDemand is "true"
                    if ("true".equalsIgnoreCase(highDemand)) {
                        // Sort by quantity in descending order (high demand items first)
                        return Integer.compare(item2.getQuantity(), item1.getQuantity());
                    } else {
                        // Default sorting (e.g., by ID or other criteria)
                        return item1.getId().compareTo(item2.getId());
                    }
                })
                .collect(Collectors.toList());

    }

    public List<Item> getAllStock() {
        return itemRepository.findAll();
    }
}
