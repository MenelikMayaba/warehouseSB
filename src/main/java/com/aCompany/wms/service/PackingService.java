package com.aCompany.wms.service;

import com.aCompany.wms.model.Product;
import com.aCompany.wms.model.Stock;
import com.aCompany.wms.model.StockTransaction;
import com.aCompany.wms.model.TransactionType;
import com.aCompany.wms.repository.StockRepository;
import com.aCompany.wms.repository.StockTransactionRepository;
import com.aCompany.wms.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PackingService {
    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockTransactionRepository transactionRepo;

    public boolean qualityCheck(Product product, int expectedQty) {
        boolean pass = product.getQuantity() >= expectedQty;

        StockTransaction tx = new StockTransaction();
        tx.setType(TransactionType.valueOf(pass ? "PACK_OK" : "PACK_FAIL"));
        tx.setQuantity(expectedQty);
        tx.setProduct(product);
        tx.setTimestamp(LocalDateTime.now());
        tx.setPerformedBy(SecurityUtil.getCurrentUsername());

        transactionRepo.save(tx);

        return pass;
    }

    public void recoverFailedPack(Product product) {
        // First get the stock for this product
        Optional<Stock> stockOpt = stockRepository.findByProduct(product);
        if (stockOpt.isPresent()) {
            Stock stock = stockOpt.get();
            stock.setQuantity(stock.getQuantity() + 1);
            stockRepository.save(stock);

            StockTransaction tx = new StockTransaction();
            tx.setType(TransactionType.PACK_RECOVERY);
            tx.setQuantity(1);
            tx.setProduct(product);
            tx.setTimestamp(LocalDateTime.now());
            tx.setPerformedBy(SecurityUtil.getCurrentUsername());

            transactionRepo.save(tx);
        } else {
            // Handle case where stock doesn't exist for this product
            throw new RuntimeException("No stock found for product: " + product.getId());
        }
    }
}
