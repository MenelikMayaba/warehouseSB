package com.aCompany.wms.service;

import com.aCompany.wms.model.Item;
import com.aCompany.wms.model.StockLocation;
import com.aCompany.wms.model.StockTransaction;
import com.aCompany.wms.repository.StockRepository;
import com.aCompany.wms.repository.StockTransactionRepository;
import com.aCompany.wms.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockService {
    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockTransactionRepository transactionRepo;

    public void putAway(Item item, StockLocation location) {
        item.setLocation(location);
        stockRepository.save(item);

        StockTransaction tx = new StockTransaction();
        tx.setType("PUTAWAY");
        tx.setQuantity(item.getQuantity());
        tx.setItem(item);
        tx.setTimestamp(LocalDateTime.now());
        tx.setPerformedBy(SecurityUtil.getCurrentUsername());

        transactionRepo.save(tx);
    }

    public List<Item> getLowStockItems() {
        return stockRepository.getLowStockItems();
    }

    /**
     * Retrieves items with optional filtering and prioritization based on search criteria.
     * @param searchTerm Optional term to filter and prioritize items (searches in item name and SKU)
     * @return List of items with matching items first, followed by others
     */
    public List<Item> getOptimizedStorage(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return stockRepository.findAll();
        }

        String searchKey = searchTerm.trim().toLowerCase();
        List<Item> allItems = stockRepository.findAll();

        return allItems.stream()
                .sorted(Comparator.comparing(item ->
                        !(item.getName().toLowerCase().contains(searchKey) ||
                                item.getSku().toLowerCase().contains(searchKey))
                ))
                .collect(Collectors.toList());
    }
}
