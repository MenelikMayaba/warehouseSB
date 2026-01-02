package com.aCompany.wms.repository;

import com.aCompany.wms.entity.Inventory;
import com.aCompany.wms.entity.Location;
import com.aCompany.wms.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CustomInventoryRepositoryImpl implements CustomInventoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Inventory> findInventoryForProduct(Product product) {
        String jpql = "SELECT i FROM Inventory i WHERE i.product = :product";
        return entityManager.createQuery(jpql, Inventory.class)
                .setParameter("product", product)
                .getResultList();
    }

    @Override
    public List<Inventory> findInventoryAtLocation(Location location) {
        String jpql = "SELECT i FROM Inventory i WHERE i.location = :location";
        return entityManager.createQuery(jpql, Inventory.class)
                .setParameter("location", location)
                .getResultList();
    }

    @Override
    public Optional<Inventory> findInventoryByProductAndLocation(Product product, Location location) {
        String jpql = "SELECT i FROM Inventory i WHERE i.product = :product AND i.location = :location";
        TypedQuery<Inventory> query = entityManager.createQuery(jpql, Inventory.class)
                .setParameter("product", product)
                .setParameter("location", location);

        List<Inventory> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public int getAvailableQuantity(Product product, Location location) {
        String jpql = "SELECT COALESCE(SUM(i.quantity), 0) FROM Inventory i " +
                "WHERE i.product = :product AND i.location = :location";
        Long result = entityManager.createQuery(jpql, Long.class)
                .setParameter("product", product)
                .setParameter("location", location)
                .getSingleResult();
        return result != null ? result.intValue() : 0;
    }

    @Override
    public List<Inventory> findLowStockItems(int threshold) {
        String jpql = "SELECT i FROM Inventory i WHERE i.quantity <= :threshold ORDER BY i.quantity ASC";
        return entityManager.createQuery(jpql, Inventory.class)
                .setParameter("threshold", threshold)
                .getResultList();
    }
}