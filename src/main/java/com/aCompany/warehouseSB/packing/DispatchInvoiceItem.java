package com.aCompany.warehouseSB.packing;

import com.aCompany.warehouseSB.inventory.Item;
import jakarta.persistence.*;

@Entity
public class DispatchInvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Item item;

    private int quantity;

    @ManyToOne
    private DispatchInvoice dispatchInvoice;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public DispatchInvoice getDispatchInvoice() {
        return dispatchInvoice;
    }

    public void setDispatchInvoice(DispatchInvoice dispatchInvoice) {
        this.dispatchInvoice = dispatchInvoice;
    }
}
