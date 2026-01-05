package com.aCompany.wms.repository;

import com.aCompany.wms.entity.ReceivingRecord;
import com.aCompany.wms.entity.ReceivingStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceivingRepository extends JpaRepository<ReceivingRecord, Long> {
    @Query("SELECT r FROM ReceivingRecord r WHERE r.status = :status AND (r.quantity - r.putAwayQuantity) > :minRemainingQuantity")
    List<ReceivingRecord> findByStatusAndRemainingQuantityGreaterThan(
            @Param("status") ReceivingStatus status, 
            @Param("minRemainingQuantity") int minRemainingQuantity);

    @EntityGraph(attributePaths = {"product"})
    @Query("SELECT r FROM ReceivingRecord r WHERE r.status = :status")
    List<ReceivingRecord> findByStatus(@Param("status") ReceivingStatus status);

    List<ReceivingRecord> findByReceivedBy(String username);

    List<ReceivingRecord> findByAllocatedLocationId(Long locationId);

    List<ReceivingRecord> findBySkuAndStatusOrderByReceivedAtAsc(String sku, ReceivingStatus status);
}

