package com.aCompany.warehouseSB.picking;

import com.aCompany.warehouseSB.inventory.Item;
import com.aCompany.warehouseSB.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Pick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Item item;

    private int quantity;

    @ManyToOne
    private User picker;

    private LocalDateTime pickedAt;
    
    @Enumerated(EnumType.STRING)
    private PickStatus status = PickStatus.PENDING;
    
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    public void setPicker(User picker) {
        this.picker = picker;
    }

    public void setPickedAt(LocalDateTime pickedAt) {
        this.pickedAt = pickedAt;
    }

    public void setQuantity(int requiredQty) {
        this.quantity = requiredQty;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public PickStatus getStatus() {
        return status;
    }

    public void setStatus(PickStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Item getItem() {
        return item;
    }

    public User getPicker() {
        return picker;
    }

    public LocalDateTime getPickedAt() {
        return pickedAt;
    }
    
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
}
