package com.aCompany.wms.repository;

import com.aCompany.wms.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    
    // Find item by product ID, location ID, and batch number
    @Query("SELECT i FROM Item i " +
           "WHERE i.product.id = :productId " +
           "AND i.location.id = :locationId " +
           "AND i.batchNumber = :batchNumber")
    Optional<Item> findByProductIdAndLocationIdAndBatchNumber(
            @Param("productId") Long productId,
            @Param("locationId") Long locationId,
            @Param("batchNumber") String batchNumber
    );
    
    // Find all items for a specific product
    List<Item> findByProductId(Long productId);
    
    // Find all items at a specific location
    List<Item> findByLocationId(Long locationId);
    
    // Find items by status
    List<Item> findByStatus(String status);
    
    // Find items with quantity less than the specified threshold
    List<Item> findByQuantityLessThan(int threshold);
    
    // Find items by product ID and status
    List<Item> findByProductIdAndStatus(Long productId, String status);
    
    // Find items by location ID and status
    List<Item> findByLocationIdAndStatus(Long locationId, String status);
    
    // Find items by SKU (case-insensitive search)
    List<Item> findBySkuContainingIgnoreCase(String sku);
    
    // Find items by batch number (case-insensitive search)
    List<Item> findByBatchNumberContainingIgnoreCase(String batchNumber);
    
    // Check if an item with the given SKU exists
    boolean existsBySku(String sku);
    
    // Find total quantity of a specific product across all locations
    @Query("SELECT COALESCE(SUM(i.quantity), 0) FROM Item i WHERE i.product.id = :productId")
    int getTotalQuantityByProductId(@Param("productId") Long productId);
    
    // Find total quantity of a specific product at a specific location
    @Query("SELECT COALESCE(SUM(i.quantity), 0) FROM Item i " +
           "WHERE i.product.id = :productId AND i.location.id = :locationId")
    int getQuantityByProductAndLocation(
            @Param("productId") Long productId,
            @Param("locationId") Long locationId
    );

    @Query("SELECT i FROM Item i " +
           "WHERE i.product.id = :productId " +
           "AND i.location.id = :locationId " +
           "AND i.quantity > 0 " +
           "ORDER BY i.expiryDate ASC, i.manufacturingDate ASC")
    List<Item> findAvailableStock(
            @Param("productId") Long productId,
            @Param("locationId") Long locationId
    );

    @Query("SELECT i FROM Item i " +
            "WHERE i.quantity > 0 AND i.quantity <= :threshold " +
            "ORDER BY i.quantity ASC")
    List<Item> getLowStockItems(@Param("threshold") int threshold);

    @Query("SELECT i FROM Item i " +
           "JOIN i.product p " +
           "WHERE LOWER(i.sku) LIKE LOWER(concat('%', :searchKey, '%')) " +
           "OR LOWER(p.name) LIKE LOWER(concat('%', :searchKey, '%'))")
    List<Item> searchByNameOrSku(@Param("searchKey") String searchKey);




}
