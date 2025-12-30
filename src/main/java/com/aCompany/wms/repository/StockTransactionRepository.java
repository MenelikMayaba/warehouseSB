package com.aCompany.wms.repository;

import com.aCompany.wms.model.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {

    @Deprecated
    @Query("SELECT t FROM StockTransaction t WHERE t.scannedBy = ?1")
    List<StockTransaction> findByPerformedBy(String username);

    @Deprecated
    @Query("SELECT t FROM StockTransaction t WHERE t.type = ?1")
    List<StockTransaction> findByType(String type);
    
    List<StockTransaction> findByType(StockTransaction.TransactionType type);

    List<StockTransaction> findByScannedByOrderByScannedAtDesc(String username);

    List<StockTransaction> findByTypeOrderByScannedAtDesc(StockTransaction.TransactionType type);

    List<StockTransaction> findByProductIdOrderByScannedAtDesc(Long productId);

    List<StockTransaction> findByScannedAtBetween(LocalDateTime start, LocalDateTime end);

    // Helper method to find transactions for a specific location
    @Query("SELECT t FROM StockTransaction t WHERE t.location.id = :locationId ORDER BY t.scannedAt DESC")
    List<StockTransaction> findByLocationIdOrderByScannedAtDesc(@Param("locationId") Long locationId);
}

