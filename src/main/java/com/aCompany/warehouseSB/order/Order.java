package com.aCompany.warehouseSB.order;

import com.aCompany.warehouseSB.user.User;
import com.aCompany.warehouseSB.packing.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders") // 'order' is a reserved SQL keyword
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String orderNumber;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.READY_FOR_PACKING;
    
    @ManyToOne
    @JoinColumn(name = "assigned_packer_id")
    private User assignedPacker;
    
    private LocalDateTime assignedAt;
    private LocalDateTime packedAt;
    
    // Add other fields as needed (e.g., customer info, items, etc.)
    
    @PrePersist
    protected void onCreate() {
        if (orderNumber == null) {
            orderNumber = "ORD-" + System.currentTimeMillis();
        }
        if (assignedAt == null) {
            assignedAt = LocalDateTime.now();
        }
    }
}
