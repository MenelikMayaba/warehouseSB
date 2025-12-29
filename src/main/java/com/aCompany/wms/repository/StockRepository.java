package com.aCompany.wms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aCompany.wms.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE i.quantity < 100")
    List<Item> getLowStockItems();

    @Query("SELECT i FROM Item i WHERE i.location.type = :type ORDER BY i.quantity DESC")
    List<Item> findItemsForOptimizedStorage(@Param("type") String type);

    Optional<Item> findById(Long id);

    List<Item> findAll();

    @Query("SELECT i FROM Item i WHERE i.quantity < 100")  // Items with quantity less than 10 are considered low stock
    List<Item> findLowStockItems();

    @Query("SELECT i FROM Item i JOIN i.product p " +
           "WHERE LOWER(i.sku) LIKE LOWER(concat('%', :searchKey, '%')) " +
           "OR LOWER(p.name) LIKE LOWER(concat('%', :searchKey, '%'))")
    List<Item> searchByNameOrSku(@Param("searchKey") String searchKey);
}
