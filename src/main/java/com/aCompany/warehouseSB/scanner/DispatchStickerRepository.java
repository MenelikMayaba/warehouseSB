package com.aCompany.warehouseSB.scanner;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DispatchStickerRepository
        extends JpaRepository<DispatchSticker, Long> {

    Optional<DispatchSticker> findByBarcode(String barcode);

    List<DispatchSticker> findByDispatchInvoiceIdAndScannedFalse(Long dispatchInvoiceId);
}

