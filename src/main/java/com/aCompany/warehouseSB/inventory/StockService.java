package com.aCompany.warehouseSB.inventory;

import com.aCompany.warehouseSB.exception.InsufficientStockException;
import com.aCompany.warehouseSB.exception.ItemNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final TransactionRepository transactionRepository;

    public StockService(StockRepository stockRepository,
                        TransactionRepository transactionRepository) {
        this.stockRepository = stockRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void addStock(Item item, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        if (item == null) throw new IllegalArgumentException("Item cannot be null");

        // Try to find existing stock for this item
        Optional<Stock> optionalStock = stockRepository.findByItem(item);
        Stock stock;

        if (optionalStock.isPresent()) {
            // Item already in stock, just increase quantity
            stock = optionalStock.get();
            stock.setQuantity(stock.getQuantity() + quantity);
        } else {
            // Item not in stock yet, create new stock entry
            stock = new Stock();
            stock.setItem(item);
            stock.setQuantity(quantity);
        }

        // Save stock changes
        stockRepository.save(stock);

        // Record the stock transaction
        StockTransaction tx = new StockTransaction();
        tx.setItem(item);
        tx.setQuantityChange(quantity); // positive number for adding stock
        tx.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(tx);
    }


    @Transactional
    public void removeStock(Item item, int quantity) {
        Optional<Stock> stockOpt = stockRepository.findByItem(item);
        if (stockOpt.isEmpty()) {
            throw new ItemNotFoundException(item.getId());
        }

        Stock stock = stockOpt.get();
        if (stock.getQuantity() < quantity) {
            throw new InsufficientStockException(item.getName());
        }

        stock.setQuantity(stock.getQuantity() - quantity);
        stockRepository.save(stock);

        StockTransaction tx = new StockTransaction();
        tx.setItem(item);
        tx.setQuantityChange(-quantity);
        tx.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(tx);
    }



    public List<Stock> getAllStock() {
        return stockRepository.findAll();
    }

    public Map<String, Integer> getStockLevelsMap() {
        return stockRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        s -> s.getItem().getName(),
                        Stock::getQuantity
                ));
    }

}
