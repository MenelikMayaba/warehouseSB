package com.aCompany.wms.repository;

import com.aCompany.wms.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByWorkflowStatus(String status);

    @Query("SELECT o FROM Order o WHERE o.workflowStatus = 'PLANNING' ORDER BY o.createdAt ASC")
    List<Order> getPlannedOrders();
}

