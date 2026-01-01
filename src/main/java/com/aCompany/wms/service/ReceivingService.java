package com.aCompany.wms.service;

import com.aCompany.wms.entity.ReceivingRecord;
import com.aCompany.wms.entity.ReceivingStatus;
import com.aCompany.wms.model.*;
import com.aCompany.wms.repository.ProductRepository;
import com.aCompany.wms.repository.ReceivingRepository;
import com.aCompany.wms.repository.StockRepository;
import com.aCompany.wms.repository.StockTransactionRepository;
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

    @Transactional
    public void receiveStock(String sku, int quantity, StockLocation receivingLocation) {
        // Validate product exists
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new RuntimeException("Product not found with SKU: " + sku));

        // Create receiving record
        ReceivingRecord record = new ReceivingRecord();
        record.setSku(sku);
        record.setProduct(product);
        record.setQuantity(quantity);
        record.setAllocatedLocation(receivingLocation);
        record.setReceivedAt(LocalDateTime.now());
        record.setReceivedBy(SecurityUtil.getCurrentUsername());
        record.setStatus(ReceivingStatus.RECEIVED);
        record.setSource("SUPPLIER");

        receivingRepository.save(record);

        // Create stock transaction
        StockTransaction tx = new StockTransaction();
        tx.setType(TransactionType.RECEIVED);
        tx.setQuantity(quantity);
        tx.setProduct(product);
        tx.setTimestamp(LocalDateTime.now());
        tx.setPerformedBy(SecurityUtil.getCurrentUsername());
        tx.setReferenceNumber("RECEIVING-" + record.getId());

        transactionRepository.save(tx);
    }

    @Transactional
    public void putAway(String sku, int quantity, StockLocation warehouseLocation) {
        // Find the oldest received but not yet put away stock
        List<ReceivingRecord> receivedStock = receivingRepository
                .findBySkuAndStatusOrderByReceivedAtAsc(sku, ReceivingStatus.RECEIVED);

        if (receivedStock.isEmpty()) {
            throw new RuntimeException("No received stock found for SKU: " + sku);
        }

        int remainingToPutAway = quantity;

        for (ReceivingRecord record : receivedStock) {
            if (remainingToPutAway <= 0) break;

            int quantityToMove = Math.min(record.getRemainingQuantity(), remainingToPutAway);

            // Update receiving record
            record.setPutAwayQuantity(record.getPutAwayQuantity() + quantityToMove);
            if (record.getPutAwayQuantity() >= record.getQuantity()) {
                record.setStatus(ReceivingStatus.PUT_AWAY);
            }
            receivingRepository.save(record);

            // Update or create stock in warehouse
            Product product = productRepository.findBySku(sku)
                    .orElseThrow(() -> new RuntimeException("Product not found with SKU: " + sku));

            Stock stock = stockRepository.findByProductIdAndLocationId(product.getId(), warehouseLocation.getId())
                    .orElseGet(() -> {
                        Stock newStock = new Stock();
                        newStock.setProduct(product);
                        newStock.setLocation(warehouseLocation);
                        newStock.setQuantity(0);
                        return newStock;
                    });

            stock.setQuantity(stock.getQuantity() + quantityToMove);
            stockRepository.save(stock);

            // Create stock transaction
            StockTransaction tx = new StockTransaction();
            tx.setType(TransactionType.PUT_AWAY);
            tx.setQuantity(quantityToMove);
            tx.setProduct(record.getProduct());
            tx.setSourceLocation(record.getAllocatedLocation().getName()); // Assuming getAllocatedLocation() returns a StockLocation with getName()
            tx.setDestinationLocation(warehouseLocation.getName()); // Assuming warehouseLocation is a StockLocation with getName()
            tx.setTimestamp(LocalDateTime.now());
            tx.setPerformedBy(SecurityUtil.getCurrentUsername());
            tx.setReferenceNumber("PUT_AWAY-REC-" + record.getId());
            transactionRepository.save(tx);

            remainingToPutAway -= quantityToMove;
        }

        if (remainingToPutAway > 0) {
            throw new RuntimeException("Insufficient received stock to put away. Remaining: " + remainingToPutAway);
        }
    }
}

