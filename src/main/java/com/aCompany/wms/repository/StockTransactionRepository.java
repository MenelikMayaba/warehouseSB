package com.aCompany.wms.repository;

import com.aCompany.wms.model.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {

    List<StockTransaction> findByPerformedBy(String username);

    List<StockTransaction> findByType(String type);
}

