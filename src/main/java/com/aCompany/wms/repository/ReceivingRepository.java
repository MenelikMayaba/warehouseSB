package com.aCompany.wms.repository;

import com.aCompany.wms.entity.ReceivingRecord;
import com.aCompany.wms.entity.ReceivingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceivingRepository extends JpaRepository<ReceivingRecord, Long> {
    List<ReceivingRecord> findByReceivedBy(String username);
    
    List<ReceivingRecord> findBySkuAndStatusOrderByReceivedAtAsc(String sku, ReceivingStatus status);
}

