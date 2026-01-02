package com.aCompany.wms.repository;

import com.aCompany.wms.entity.Inventory;
import com.aCompany.wms.entity.Location;
import com.aCompany.wms.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductAndLocation(Product product, Location location);

    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId AND i.location.id = :locationId")
    Optional<Inventory> findByProductIdAndLocationId(@Param("productId") Long productId, @Param("locationId") Long locationId);

    List<Inventory> findByProduct(Product product);
    List<Inventory> findByLocation(Location location);
    List<Inventory> findByStatus(Inventory.InventoryStatus status);

    @Query("SELECT p.storageType, SUM(i.quantity * i.product.price) " +
            "FROM Inventory i JOIN i.product p " +
            "GROUP BY p.storageType")
    List<Object[]> getInventoryValueByCategory();

    @Query("SELECT i FROM Inventory i WHERE " +
            "(:productId IS NULL OR i.product.id = :productId) AND " +
            "(:locationId IS NULL OR i.location.id = :locationId) AND " +
            "(:status IS NULL OR i.status = :status)")
    Page<Inventory> searchInventory(
            @Param("productId") Long productId,
            @Param("locationId") Long locationId,
            @Param("status") Inventory.InventoryStatus status,
            Pageable pageable);

    @Query("SELECT i.product, SUM(i.quantity * i.product.price) as totalValue " +
            "FROM Inventory i " +
            "GROUP BY i.product " +
            "ORDER BY totalValue DESC")
    List<Object[]> getTopValuableItems(@Param("limit") int limit);

    @Query("SELECT i.location, COUNT(i) FROM Inventory i GROUP BY i.location")
    List<Object[]> getInventoryCountByLocation();

    @Query("SELECT SUM(i.quantity * i.product.price) FROM Inventory i")
    Optional<BigDecimal> calculateTotalInventoryValue();

    @Query("SELECT t.type, COUNT(t) " +
            "FROM InventoryTransaction t " +
            "WHERE t.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY t.type")
    List<Object[]> getMovementCountsByType(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COALESCE(t.toLocation, t.fromLocation) as location, COUNT(t) as movementCount " +
            "FROM InventoryTransaction t " +
            "WHERE t.createdAt BETWEEN :startDate AND :endDate " +
            "AND (t.toLocation IS NOT NULL OR t.fromLocation IS NOT NULL) " +
            "GROUP BY COALESCE(t.toLocation, t.fromLocation) " +
            "ORDER BY movementCount DESC")
    Page<Object[]> getBusiestLocations(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);



    @Query("SELECT COUNT(t) FROM InventoryTransaction t " +
           "WHERE t.createdAt BETWEEN :startDate AND :endDate")
    long countMovementsInDateRange(
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);



    @Query(value = "SELECT t.product, COUNT(t) as movementCount, SUM(CASE WHEN t.type = 'RECEIVE' THEN t.quantity ELSE -t.quantity END) as netChange " +
            "FROM InventoryTransaction t " +
            "WHERE t.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY t.product " +
            "ORDER BY movementCount DESC " +
            "LIMIT :limit", 
            nativeQuery = true)
    List<Object[]> getMostMovedProducts(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("limit") int limit);

    @Query("SELECT i FROM Inventory i " +
            "WHERE i.quantity > 0 AND i.quantity <= :threshold " +
            "ORDER BY i.quantity ASC")
    List<Inventory> findLowStockItems(@Param("threshold") int threshold);

    @Query(value = "SELECT i.product, i.location, i.quantity, i.updated_at, " +
            "DATEDIFF(CURRENT_DATE, i.updated_at) AS daysInStock " +
            "FROM inventory i " +
            "WHERE i.quantity > 0 " +
            "ORDER BY daysInStock DESC",
            nativeQuery = true)
    List<Object[]> getInventoryAgingReport();
}