package com.aCompany.wms.repository;

import com.aCompany.wms.model.StockTransaction;
import com.aCompany.wms.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    
    List<StockTransaction> findByType(TransactionType type);

    List<StockTransaction> findByScannedByOrderByScannedAtDesc(String username);

    List<StockTransaction> findByTypeOrderByScannedAtDesc(TransactionType type);

    List<StockTransaction> findByProductIdOrderByScannedAtDesc(Long productId);

    List<StockTransaction> findByScannedAtBetween(LocalDateTime start, LocalDateTime end);

    // Helper method to find transactions for a specific location
    @Query("SELECT t FROM StockTransaction t WHERE t.location.id = :locationId ORDER BY t.scannedAt DESC")
    List<StockTransaction> findByLocationIdOrderByScannedAtDesc(@Param("locationId") Long locationId);
    Page<StockTransaction> findByScannedBy(String username, Pageable pageable);

    Page<StockTransaction> findByType(TransactionType type, Pageable pageable);

    Page<StockTransaction> findByScannedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("SELECT t FROM StockTransaction t WHERE " +
            "(:user IS NULL OR t.scannedBy = :user) AND " +
            "(:type IS NULL OR t.type = :type) AND " +
            "(cast(:startDate as timestamp) IS NULL OR t.scannedAt >= :startDate) AND " +
            "(cast(:endDate as timestamp) IS NULL OR t.scannedAt <= :endDate)")
    Page<StockTransaction> search(
            @Param("user") String user,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

}

