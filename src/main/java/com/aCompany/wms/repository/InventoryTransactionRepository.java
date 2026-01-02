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
}
