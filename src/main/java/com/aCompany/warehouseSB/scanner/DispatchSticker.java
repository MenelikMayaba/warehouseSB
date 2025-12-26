package com.aCompany.warehouseSB.scanner;

import com.aCompany.warehouseSB.inventory.Item;
import com.aCompany.warehouseSB.packing.DispatchInvoice;
import com.aCompany.warehouseSB.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class DispatchSticker {

    @Id
    @GeneratedValue
    private Long id;

    private String barcode;

    private int stickerIndex;   // 1
    private int stickerTotal;   // 5

    private boolean scanned;
    private LocalDateTime scannedAt;

    @ManyToOne
    private User scannedBy;

    @ManyToOne
    private DispatchInvoice dispatchInvoice;

    @ManyToOne
    private Item item;
    private int quantity;
    private LocalDateTime createdAt;

    public void setScanned(boolean b) {
        this.scanned = b;
    }

    public boolean isScanned() {
        return scanned;
    }

    public void setScannedAt(LocalDateTime now) {
        this.scannedAt = now;
    }


    public void setScannedBy(User scanner) {
        this.scannedBy = scanner;
    }

    public String getStickerTotal() {
        return String.valueOf(stickerTotal);
    }

    public String getStickerIndex() {
        return String.valueOf(stickerIndex);
    }

    public void setBarcode(String string) {
        this.barcode = string;
    }

    public void setDispatchInvoice(DispatchInvoice dispatch) {
        this.dispatchInvoice = dispatch;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setCreatedAt(LocalDateTime now) {
        this.createdAt = now;
    }
}


