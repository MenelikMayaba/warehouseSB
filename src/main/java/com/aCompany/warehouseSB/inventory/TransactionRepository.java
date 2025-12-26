package com.aCompany.warehouseSB.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository
        extends JpaRepository<StockTransaction, Long> {

}