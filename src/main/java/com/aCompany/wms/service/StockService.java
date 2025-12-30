package com.aCompany.wms.service;

import com.aCompany.wms.dto.StockAdjustmentDto;
import com.aCompany.wms.dto.StockReceiptDto;
import com.aCompany.wms.exceptions.*;
import com.aCompany.wms.model.Product;
import com.aCompany.wms.model.Stock;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StockService {

    private final StockTransactionRepository transactionRepo;
    private final ProductRepository productRepository;
    private final StockLocationRepository locationRepository;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;

    @Autowired
    public StockService(
                        StockTransactionRepository transactionRepo,
                        ProductRepository productRepository,
                        StockLocationRepository locationRepository,
                        UserRepository userRepository, StockRepository stockRepository) {

        this.transactionRepo = transactionRepo;
        this.productRepository = productRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
    }

    @PreAuthorize("hasRole('RECEIVER')")
    public Product receiveStock(StockReceiptDto receipt) {
        // Find or create stock entry
        Product item = productRepository.findByProductIdAndLocationIdAndBatchNumber(
                receipt.productId(),
                receipt.locationId(),
                receipt.batchNumber()
        ).orElseGet(() -> {
            Product newProduct = new Product();
            try {
                newProduct.setProduct(productRepository.findById(receipt.productId())
                        .orElseThrow(() -> new ProductNotFoundException("Product not found")));
            } catch (ProductNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                newProduct.setLocation(locationRepository.findById(receipt.locationId())
                        .orElseThrow(() -> new LocationNotFoundException("Location not found")));
            } catch (LocationNotFoundException e) {
                throw new RuntimeException(e);
            }
            newProduct.setBatchNumber(receipt.batchNumber());
            newProduct.setExpiryDate(receipt.expiryDate().atStartOfDay());
            return newProduct;
        });

        // Update quantity
        item.setQuantity(item.getQuantity() + receipt.quantity());
        item.setLastUpdated(LocalDateTime.now());
        productRepository.save(item);

        // Log transaction
        logTransaction(item, "RECEIVED", receipt.quantity(), receipt.referenceNumber(), receipt.notes());

        return item;
    }


    @PreAuthorize("hasRole('PICKER')")
    @Transactional
    public Stock pickStock(StockPickDto pick) throws StockNotFoundException, InsufficientStockException {
        // Find all available stock items for the product and location
        List<Stock> availableStocks = stockRepository.findAvailableStock(
                pick.productId(),
                pick.locationId()
        );

        if (availableStocks.isEmpty()) {
            throw new StockNotFoundException("No available stock found for the specified product and location");
        }

        // Find the first item with sufficient quantity
        Stock selectedStock = availableStocks.stream()
                .filter(item -> item.getQuantity() >= pick.quantity())
                .findFirst()
                .orElseThrow(() -> {
                    int totalAvailable = availableStocks.stream()
                            .mapToInt(Stock::getQuantity)
                            .sum();
                    return new InsufficientStockException(
                            String.format("Insufficient stock. Total available: %d, Requested: %d",
                                    totalAvailable, pick.quantity())
                    );
                });

        // Update quantity
        int newQuantity = selectedStock.getQuantity() - pick.quantity();
        selectedStock.setQuantity(newQuantity);
        selectedStock.setUpdatedAt(LocalDateTime.now());
        
        // Save the updated stock
        stockRepository.save(selectedStock);
        
        // Log the transaction (quantity is negative for picking/removing stock)
        logTransaction(selectedStock.getProduct(), "PICKED", -pick.quantity(), 
                pick.referenceNumber(), pick.notes());

        return selectedStock;
    }

    @PreAuthorize("hasAnyRole('RECEIVER', 'PICKER', 'ADMIN')")
    public List<Stock> getStockByProduct(Long productId) {
        return stockRepository.findByProductId(productId);
    }

    @PreAuthorize("hasAnyRole('RECEIVER', 'PICKER', 'ADMIN')")
    public List<Stock> getStockByLocation(Long locationId) {
        return stockRepository.findByLocationId(locationId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void adjustStock(StockAdjustmentDto adjustment) throws ItemNotFoundException {
        // Find the item
        Product product = productRepository.findById(adjustment.itemId())
                .orElseThrow(() -> new ItemNotFoundException("Item not found with ID: " + adjustment.itemId()));

        // Calculate new quantity
        int newQuantity = product.getQuantity() + adjustment.adjustment();
        if (newQuantity < 0) {
            throw new InsufficientStockException("Cannot adjust stock below zero");
        }

        // Update item quantity
        product.setQuantity(newQuantity);
        product.setLastUpdated(LocalDateTime.now());
        productRepository.save(product);

        // Log the adjustment
        logTransaction(
                product,
                "ADJUST",
                adjustment.adjustment(),
                adjustment.referenceNumber(),
                "Reason: " + adjustment.reason() + ". " + (adjustment.notes() != null ? adjustment.notes() : "")
        );
    }



    public void putAway(Product product, StockLocation location) {
        product.setLocation(location);
        productRepository.save(product);

        StockTransaction tx = new StockTransaction();
        tx.setType(StockTransaction.TransactionType.PUTAWAY);
        tx.setQuantity(product.getQuantity());
        tx.setProduct(product);
        tx.setLocation(location);
        tx.setTimestamp(LocalDateTime.now());
        tx.setScannedAt(LocalDateTime.now());
        tx.setScannedBy(SecurityUtil.getCurrentUsername());
        tx.setAction("PUTAWAY");

        transactionRepo.save(tx);
    }

    private void logTransaction(Product product, String type, int quantity, String reference, String notes) {
        StockTransaction tx = new StockTransaction();
        try {
            tx.setType(StockTransaction.TransactionType.valueOf(type));
        } catch (IllegalArgumentException e) {
            tx.setType(StockTransaction.TransactionType.ADJUSTED); // Default type if not found
        }
        tx.setProduct(product);
        tx.setQuantity(quantity);
        tx.setReferenceNumber(reference);
        tx.setNotes(notes);
        LocalDateTime now = LocalDateTime.now();
        tx.setTimestamp(now);  // Main transaction timestamp
        tx.setScannedAt(now);  // When the scan was performed
        tx.setScannedBy(SecurityUtil.getCurrentUsername());
        tx.setAction(type); // Store the original string as action

        // If you have location information, set it here
        // tx.setLocation(product.getLocation());

        transactionRepo.save(tx);
    }

    // Existing methods with improvements
    public List<Stock> getLowStockItems() {
        return stockRepository.findLowStockItems();
    }

    public List<Product> searchItems(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return productRepository.findAll();
        }

        String searchKey = searchTerm.trim().toLowerCase();
        return productRepository.searchByNameOrSku(searchKey);
    }

    public List<Product> getOptimizedStorage(String highDemand) {
        // Get all items from the repository
        List<Product> allItems = productRepository.findAll();

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

    public List<Product> getAllStock() {
        return productRepository.findAll();
    }

    public void addStock(Stock stock) {
        stockRepository.save(stock);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void removeStock(Long productId, Long locationId, int quantity, String batchNumber) throws ProductNotFoundException, LocationNotFoundException, StockNotFoundException {
        // First verify the product and location exist
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException("Product not found with ID: " + productId);
        }
        if (!locationRepository.existsById(locationId)) {
            throw new LocationNotFoundException("Location not found with ID: " + locationId);
        }

        // Then find the stock entry
        Stock stock = (Stock) stockRepository.findByProductIdAndLocationIdAndBatchNumber(
                        productId, locationId, batchNumber)
                .orElseThrow(() -> new StockNotFoundException(
                        String.format("No stock found for product ID %d at location %d with batch %s",
                                productId, locationId, batchNumber != null ? batchNumber : "N/A")));

        // Rest of the method remains the same
        if (stock.getQuantity() < quantity) {
            throw new InsufficientStockException(
                    String.format("Insufficient stock. Available: %d, Requested to remove: %d",
                            stock.getQuantity(), quantity)
            );
        }

        stock.setQuantity(stock.getQuantity() - quantity);
        stock.setUpdatedAt(LocalDateTime.now());
        stockRepository.save(stock);

        logTransaction(
                stock.getProduct(),
                "REMOVED",
                -quantity,
                "MANUAL_REMOVAL",
                "Manual stock removal" + (batchNumber != null ? ", Batch: " + batchNumber : "")
        );
    }


}
