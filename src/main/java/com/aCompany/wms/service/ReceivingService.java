package com.aCompany.wms.service;

import com.aCompany.wms.entity.ReceivingRecord;
import com.aCompany.wms.model.Item;
import com.aCompany.wms.model.StockLocation;
import com.aCompany.wms.model.StockTransaction;
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
    private ReceivingRepository receivingRepository;

    @Autowired
    private StockTransactionRepository transactionRepository;

    public void receiveStock(String sku, int quantity, StockLocation location) {

        Item item = stockRepository.findAll()
                .stream()
                .filter(i -> i.getSku().equals(sku))
                .findFirst()
                .orElseGet(() -> {
                    Item newItem = new Item();
                    newItem.setSku(sku);
                    newItem.setQuantity(0);
                    newItem.setLocation(location);
                    return newItem;
                });

        item.setQuantity(item.getQuantity() + quantity);
        item.setLocation(location);
        stockRepository.save(item);

        ReceivingRecord record = new ReceivingRecord();
        record.setSku(sku);
        record.setQuantity(quantity);
        record.setAllocatedLocation(location);
        record.setReceivedAt(LocalDateTime.now());
        record.setReceivedBy(SecurityUtil.getCurrentUsername());
        record.setSource("SUPPLIER");

        receivingRepository.save(record);

        StockTransaction tx = new StockTransaction();
        tx.setType("RECEIVE");
        tx.setQuantity(quantity);
        tx.setItem(item);
        tx.setTimestamp(LocalDateTime.now());
        tx.setPerformedBy(SecurityUtil.getCurrentUsername());

        transactionRepository.save(tx);
    }
}

