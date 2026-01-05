package com.aCompany.wms.service;

import com.aCompany.wms.entity.Location;
import com.aCompany.wms.entity.LocationType;
import com.aCompany.wms.entity.ReceivingRecord;
import com.aCompany.wms.entity.ReceivingStatus;
import com.aCompany.wms.model.TransactionType;
import com.aCompany.wms.model.*;
import com.aCompany.wms.repository.*;
import com.aCompany.wms.util.SecurityUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReceivingService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ReceivingRepository receivingRepository;

    @Autowired
    private StockTransactionRepository transactionRepository;

    @Autowired
    private LocationRepository locationRepository;

    private int newRemainingQuantity;


    @Transactional
    public void receiveStock(String sku, int quantity, Long locationId, String username) {
        // Find the product by SKU
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Product not found with SKU: " + sku));

        // Find the receiving location
        Location receivingLocation = locationRepository.findByType(LocationType.RECEIVING)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Receiving location not found"));

        // Create a new receiving record
        ReceivingRecord receivingRecord = new ReceivingRecord();
        receivingRecord.setSku(sku);
        receivingRecord.setProduct(product);
        receivingRecord.setQuantity(quantity);
        receivingRecord.setSource("MANUAL_RECEIPT");
        receivingRecord.setReceivedAt(LocalDateTime.now());
        receivingRecord.setReceivedBy(username);
        receivingRecord.setStatus(ReceivingStatus.RECEIVED);
        receivingRecord.setAllocatedLocation(receivingLocation);

        // Save the receiving record
        receivingRepository.save(receivingRecord);

        // Log the transaction
        StockTransaction transaction = new StockTransaction();
        transaction.setProduct(product);
        transaction.setQuantity(quantity);
        transaction.setType(TransactionType.RECEIVED);
        transaction.setReferenceNumber("RECEIVING-" + receivingRecord.getId());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setScannedBy(username);
        transaction.setLocation(receivingLocation);

        transactionRepository.save(transaction);
    }

    @Transactional
    public void putAway(Long receivingRecordId, Long locationId, int quantity, String username) {
        // Find the receiving record
        ReceivingRecord receivingRecord = receivingRepository.findById(receivingRecordId)
                .orElseThrow(() -> new RuntimeException("Receiving record not found"));

        // Validate the quantity
        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than zero");
        }

        if (quantity > receivingRecord.getRemainingQuantity()) {
            throw new RuntimeException("Cannot put away more than the remaining quantity");
        }

        // Find the destination location
        Location destinationLocation = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Destination location not found"));

        // Find or create stock in the destination location
        Stock stock = stockRepository.findBySkuAndLocationId(receivingRecord.getSku(), locationId)
                .orElseGet(() -> {
                    Stock newStock = new Stock();
                    newStock.setSku(receivingRecord.getSku());
                    newStock.setProduct(receivingRecord.getProduct());
                    newStock.setLocation(destinationLocation);
                    newStock.setQuantity(0);
                    return newStock;
                });

        // Update the stock quantity
        stock.setQuantity(stock.getQuantity() + quantity);
        stockRepository.save(stock);

        // Update the receiving record by increasing putAwayQuantity
        receivingRecord.setPutAwayQuantity(receivingRecord.getPutAwayQuantity() + quantity);

        // If all quantity has been put away, mark as complete
        if (receivingRecord.getRemainingQuantity() == 0) {
            receivingRecord.setStatus(ReceivingStatus.PUT_AWAY_COMPLETE);
        }

        receivingRepository.save(receivingRecord);

        // Log the transaction
        StockTransaction transaction = new StockTransaction();
        transaction.setProduct(receivingRecord.getProduct());
        transaction.setQuantity(quantity);
        transaction.setType(TransactionType.PUT_AWAY);
        transaction.setReferenceNumber("PUT-AWAY-" + receivingRecord.getId());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setScannedBy(username);
        transaction.setLocation(destinationLocation);

        transactionRepository.save(transaction);
    }

}

