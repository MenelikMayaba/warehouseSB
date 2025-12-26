package com.aCompany.warehouseSB.packing;

import com.aCompany.warehouseSB.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    
    /**
     * Find all orders with status in the given list of statuses
     * @param statuses List of order statuses to search for
     * @return Collection of orders matching the status criteria
     */
    Collection<Order> findByStatusIn(List<OrderStatus> statuses);
    
    /**
     * Find an order by its order number
     * @param orderNumber The order number to search for
     * @return The order if found, null otherwise
     */
    Optional<Order> findByOrderNumber(String orderNumber);
    
    /**
     * Find all orders that are ready for packing
     * @return Collection of orders ready for packing
     */
    default Collection<Order> findReadyForPacking() {

        return findByStatusIn(List.of(OrderStatus.READY_FOR_PACKING));
    }
    
    /**
     * Find all orders that are currently being packed
     * @return Collection of orders in progress
     */
    default Collection<Order> findInProgress() {
        return findByStatusIn(List.of(OrderStatus.PACKING_IN_PROGRESS));
    }
}
