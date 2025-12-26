package com.aCompany.warehouseSB.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByItem(Item item);


    // No need to declare save() method as it's already provided by JpaRepository
}