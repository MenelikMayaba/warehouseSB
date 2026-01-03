package com.aCompany.wms.repository;

import com.aCompany.wms.model.Product;
import com.aCompany.wms.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);

    // Find products by name or SKU (case-insensitive search)
    @Query("SELECT p FROM Product p " +
            "WHERE LOWER(p.sku) LIKE LOWER(concat('%', :searchKey, '%')) " +
            "OR LOWER(p.name) LIKE LOWER(concat('%', :searchKey, '%'))")
    List<Product> searchByNameOrSku(@Param("searchKey") String searchKey);

    // Find products by status
    List<Product> findByStatus(String status);


    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantityInStock < :threshold")
    long countByQuantityInStockLessThan(@Param("threshold") int threshold);


    @Query("SELECT p FROM Product p " +
            "WHERE LOWER(p.sku) LIKE LOWER(concat('%', :searchKey, '%')) " +
            "OR LOWER(p.name) LIKE LOWER(concat('%', :searchKey, '%'))")
    List<Product> searchProducts(@Param("searchKey") String searchKey);

    // Find products with quantity less than the specified threshold
    @Query("SELECT p FROM Product p WHERE p.quantityInStock < :threshold")
    List<Product> findByQuantityLessThan(@Param("threshold") int threshold);

    // Find products by SKU (case-insensitive search)
    List<Product> findBySkuContainingIgnoreCase(String sku);

    // Check if a product with the given SKU exists
    boolean existsBySku(String sku);

    // Find total quantity of a specific product across all locations
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Stock s WHERE s.product.id = :productId")
    int getTotalQuantityByProductId(@Param("productId") Long productId);

    // Find total quantity of a specific product at a specific location
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Stock s " +
            "WHERE s.product.id = :productId AND s.location.id = :locationId")
    int getQuantityByProductAndLocation(
            @Param("productId") Long productId,
            @Param("locationId") Long locationId
    );

    // Find available stock for a product at a location
    @Query("SELECT s FROM Stock s " +
            "WHERE s.product.id = :productId " +
            "AND s.location.id = :locationId " +
            "AND s.quantity > 0 " +
            "ORDER BY s.expiryDate ASC NULLS LAST, s.manufacturingDate ASC NULLS LAST")
    List<Stock> findAvailableStock(
            @Param("productId") Long productId,
            @Param("locationId") Long locationId
    );

    // Find low stock items
    @Query("SELECT s FROM Stock s " +
            "WHERE s.quantity > 0 AND s.quantity <= :threshold " +
            "ORDER BY s.quantity ASC")
    List<Stock> getLowStockItems(@Param("threshold") int threshold);

    Optional<Product> findByProductIdAndLocationIdAndBatchNumber(Long aLong, Long aLong1, String s);

    List<Product> findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(String query, String query1);
}