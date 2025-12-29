package com.aCompany.wms.repository;

import com.aCompany.wms.entity.ReceivingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceivingRepository extends JpaRepository<ReceivingRecord, Long> {
    List<ReceivingRecord> findByReceivedBy(String username);
}

