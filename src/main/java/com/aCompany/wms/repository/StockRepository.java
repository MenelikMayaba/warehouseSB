package com.aCompany.wms.repository;

import com.aCompany.wms.model.Product;
import com.aCompany.wms.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    @Query("SELECT s FROM Stock s LEFT JOIN FETCH s.product p LEFT JOIN FETCH s.location l")
    List<Stock> findAllWithProductAndLocation();

    @Query("SELECT s FROM Stock s WHERE s.product.sku = :sku AND s.location.id = :locationId")
    Optional<Stock> findBySkuAndLocationId(@Param("sku") String sku, @Param("locationId") Long locationId);

    Optional<Stock> findByProduct(Product product);
    
    @Query("SELECT s FROM Stock s WHERE s.quantity < (SELECT p.reorderLevel FROM Product p WHERE p = s.product)")
    List<Stock> findLowStockItems();
    
    @Query("SELECT s FROM Stock s WHERE s.product.storageType = :type")
    List<Stock> findItemsForOptimizedStorage(@Param("type") String type);
    
    Optional<Stock> findById(Long id);
    
    List<Stock> findAll();
    
    @Query("SELECT s FROM Stock s WHERE LOWER(s.product.name) LIKE LOWER(concat('%', :searchKey, '%')) OR LOWER(s.product.sku) LIKE LOWER(concat('%', :searchKey, '%'))")
    List<Stock> searchByNameOrSku(@Param("searchKey") String searchKey);
    
    List<Stock> findByProductId(Long productId);
    
    @Query("SELECT s FROM Stock s WHERE s.product.id = :productId AND s.location.id = :locationId AND s.quantity > 0")
    List<Stock> findAvailableStock(@Param("productId") Long productId, @Param("locationId") Long locationId);
    
    List<Stock> findByLocationId(Long locationId);
    
    Optional<Stock> findByProductIdAndLocationId(Long productId, Long locationId);
    
    @Query("SELECT SUM(s.quantity) FROM Stock s WHERE s.product.id = :productId")
    Integer getTotalQuantityByProductId(@Param("productId") Long productId);
    
    @Query("SELECT s FROM Stock s WHERE s.quantity > 0 AND s.product.id = :productId ORDER BY s.expiryDate ASC")
    List<Stock> findAvailableStockByProductIdOrderByExpiryDate(@Param("productId") Long productId);

    @Query("SELECT s FROM Stock s WHERE s.product.id = :productId AND s.location.id = :locationId AND s.batchNumber = :batchNumber")
    Optional<Stock> findByProductIdAndLocationIdAndBatchNumber(@Param("productId") Long productId, @Param("locationId") Long locationId, @Param("batchNumber") String batchNumber);

    // If you have createdAt field:
    List<Stock> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Or if you have updatedAt field:
    List<Stock> findByUpdatedAtBetween(LocalDateTime start, LocalDateTime end);
    @Query("SELECT s FROM Stock s LEFT JOIN FETCH s.location")
    List<Stock> findAllWithLocation();
}
