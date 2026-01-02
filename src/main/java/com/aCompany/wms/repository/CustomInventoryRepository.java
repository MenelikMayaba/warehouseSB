package com.aCompany.wms.repository;

import com.aCompany.wms.entity.Inventory;
import com.aCompany.wms.entity.Location;
import com.aCompany.wms.model.Product;

import java.util.List;
import java.util.Optional;

public interface CustomInventoryRepository {
    List<Inventory> findInventoryForProduct(Product product);
    List<Inventory> findInventoryAtLocation(Location location);
    Optional<Inventory> findInventoryByProductAndLocation(Product product, Location location);
    int getAvailableQuantity(Product product, Location location);
    List<Inventory> findLowStockItems(int threshold);
}
