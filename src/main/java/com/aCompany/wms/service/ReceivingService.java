package com.aCompany.wms.service;

import com.aCompany.wms.entity.ReceivingRecord;
import com.aCompany.wms.model.Product;
import com.aCompany.wms.model.Stock;
import com.aCompany.wms.model.StockLocation;
import com.aCompany.wms.model.StockTransaction;
import com.aCompany.wms.repository.ProductRepository;
import com.aCompany.wms.repository.ReceivingRepository;
import com.aCompany.wms.repository.StockRepository;
import com.aCompany.wms.repository.StockTransactionRepository;
import com.aCompany.wms.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    public void receiveStock(String sku, int quantity, StockLocation location) {

        Stock stock = stockRepository.findAll()
                .stream()
                .filter(i -> i.getSku().equals(sku))
                .findFirst()
                .orElseGet(() -> {
                    Stock newStock = new Stock();
                    newStock.setSku(sku);
                    newStock.setQuantity(0);
                    newStock.setLocation(location);
                    return newStock;
                });

        stock.setQuantity(stock.getQuantity() + quantity);
        stock.setLocation(location);
        stockRepository.save(stock);

        ReceivingRecord record = new ReceivingRecord();
        record.setSku(sku);
        record.setQuantity(quantity);
        record.setAllocatedLocation(location);
        record.setReceivedAt(LocalDateTime.now());
        record.setReceivedBy(SecurityUtil.getCurrentUsername());
        record.setSource("SUPPLIER");

        receivingRepository.save(record);

        // Retrieve the product by SKU
        Product product = productRepository.findBySku(sku)
            .orElseThrow(() -> new RuntimeException("Product not found with SKU: " + sku));
            
        StockTransaction tx = new StockTransaction();
        tx.setType(StockTransaction.TransactionType.RECEIVED);
        tx.setQuantity(quantity);
        tx.setProduct(product);
        tx.setTimestamp(LocalDateTime.now());
        tx.setPerformedBy(SecurityUtil.getCurrentUsername());

        transactionRepository.save(tx);
    }
}

