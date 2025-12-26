package com.aCompany.warehouseSB.scanner;

import com.aCompany.warehouseSB.packing.DispatchInvoice;
import com.aCompany.warehouseSB.inventory.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CageScanRepository extends JpaRepository<CageScan, Long> {
    List<CageScan> findByDispatchInvoice(DispatchInvoice invoice);
    List<CageScan> findByDispatchInvoiceAndItem(DispatchInvoice invoice, Item item);
    List<CageScan> findByDispatchInvoiceId(Long dispatchInvoiceId);
}
