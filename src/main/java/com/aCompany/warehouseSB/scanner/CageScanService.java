package com.aCompany.warehouseSB.scanner;

import com.aCompany.warehouseSB.packing.DispatchInvoice;
import com.aCompany.warehouseSB.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CageScanService {

    private final DispatchStickerRepository stickerRepository;

    public CageScanService(DispatchStickerRepository stickerRepository) {
        this.stickerRepository = stickerRepository;
    }

    public DispatchInvoice scanSticker(String barcode, User scanner) {

        DispatchSticker sticker = stickerRepository.findByBarcode(barcode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid sticker barcode"));

        if (sticker.isScanned()) {
            throw new IllegalStateException(
                    "Sticker " + sticker.getStickerIndex() + "/" +
                            sticker.getStickerTotal() + " already scanned"
            );
        }

        sticker.setScanned(true);
        sticker.setScannedAt(LocalDateTime.now());
        sticker.setScannedBy(scanner);

        stickerRepository.save(sticker);
        return null;
    }

    public List<DispatchSticker> getMissingStickers(Long dispatchInvoiceId) {
        return stickerRepository
                .findByDispatchInvoiceIdAndScannedFalse(dispatchInvoiceId);
    }

    public boolean isInvoiceFullyScanned(Long dispatchInvoiceId) {
        return getMissingStickers(dispatchInvoiceId).isEmpty();
    }

}

