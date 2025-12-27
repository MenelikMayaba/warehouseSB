package com.aCompany.wms.service;

import com.aCompany.wms.model.Item;
import com.aCompany.wms.model.StockTransaction;
import com.aCompany.wms.repository.StockRepository;
import com.aCompany.wms.repository.StockTransactionRepository;
import com.aCompany.wms.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PackingService {
    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockTransactionRepository transactionRepo;

    public boolean qualityCheck(Item item, int expectedQty) {
        boolean pass = item.getQuantity() >= expectedQty;

        StockTransaction tx = new StockTransaction();
        tx.setType(pass ? "PACK_OK" : "PACK_FAIL");
        tx.setQuantity(expectedQty);
        tx.setItem(item);
        tx.setTimestamp(LocalDateTime.now());
        tx.setPerformedBy(SecurityUtil.getCurrentUsername());

        transactionRepo.save(tx);

        return pass;
    }

    public void recoverFailedPack(Item item) {
        item.setQuantity(item.getQuantity() + 1);
        stockRepository.save(item);

        StockTransaction tx = new StockTransaction();
        tx.setType("PACK_RECOVERY");
        tx.setQuantity(1);
        tx.setItem(item);
        tx.setTimestamp(LocalDateTime.now());
        tx.setPerformedBy(SecurityUtil.getCurrentUsername());

        transactionRepo.save(tx);
    }
}
