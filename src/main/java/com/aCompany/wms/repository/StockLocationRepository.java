package com.aCompany.wms.repository;

import com.aCompany.wms.model.StockLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockLocationRepository extends JpaRepository<StockLocation, Long> {
    Optional<StockLocation> findById(Long id);
}
