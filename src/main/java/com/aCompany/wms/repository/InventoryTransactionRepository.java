package com.aCompany.wms.repository;

import com.aCompany.wms.entity.InventoryTransaction;
import com.aCompany.wms.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    List<InventoryTransaction> findByProduct(Product product);
    List<InventoryTransaction> findByReferenceId(String referenceId);

    @Query("SELECT t FROM InventoryTransaction t WHERE " +
            "(:productId IS NULL OR t.product.id = :productId) AND " +
            "(:startDate IS NULL OR t.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR t.createdAt <= :endDate) AND " +
            "(:type IS NULL OR t.type = :type)")
    Page<InventoryTransaction> searchTransactions(
            @Param("productId") Long productId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("type") InventoryTransaction.TransactionType type,
            Pageable pageable);

    @Query("SELECT t.type, COUNT(t) " +
            "FROM InventoryTransaction t " +
            "WHERE t.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY t.type")
    List<Object[]> getMovementCountsByType(
            @Param("startDate") java.time.LocalDate startDate,
            @Param("endDate") java.time.LocalDate endDate
    );

    @Query("SELECT COALESCE(t.toLocation, t.fromLocation) as location, COUNT(t) as movementCount " +
            "FROM InventoryTransaction t " +
            "WHERE t.createdAt BETWEEN :startDate AND :endDate " +
            "AND (t.toLocation IS NOT NULL OR t.fromLocation IS NOT NULL) " +
            "GROUP BY COALESCE(t.toLocation, t.fromLocation) " +
            "ORDER BY movementCount DESC")
    Page<Object[]> getBusiestLocations(
            @Param("startDate") java.time.LocalDate startDate,
            @Param("endDate") java.time.LocalDate endDate,
            Pageable pageable);

    @Query("SELECT COUNT(t) FROM InventoryTransaction t " +
           "WHERE t.createdAt BETWEEN :startDate AND :endDate")
    long countMovementsInDateRange(
            @Param("startDate") java.time.LocalDate startDate,
            @Param("endDate") java.time.LocalDate endDate);

    @Query(value = "SELECT t.product, COUNT(t) as movementCount, SUM(CASE WHEN t.type = 'RECEIVE' THEN t.quantity ELSE -t.quantity END) as netChange " +
            "FROM inventory_transaction t " +
            "WHERE t.created_at BETWEEN :startDate AND :endDate " +
            "GROUP BY t.product " +
            "ORDER BY movementCount DESC " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Object[]> getMostMovedProducts(
            @Param("startDate") java.time.LocalDate startDate,
            @Param("endDate") java.time.LocalDate endDate,
            @Param("limit") int limit);
}
