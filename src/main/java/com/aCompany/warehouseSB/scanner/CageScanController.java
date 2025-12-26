package com.aCompany.warehouseSB.scanner;

import com.aCompany.warehouseSB.packing.DispatchInvoice;
import com.aCompany.warehouseSB.inventory.Item;
import com.aCompany.warehouseSB.packing.DispatchInvoiceRepository;
import com.aCompany.warehouseSB.user.User;
import com.aCompany.warehouseSB.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/scanner")
public class CageScanController {

    private final CageScanService cageScanService;

    public CageScanController(CageScanService cageScanService) {
        this.cageScanService = cageScanService;
    }

    @PostMapping("/scan")
    @PreAuthorize(
            "hasAuthority('SCANNER') or " +
                    "hasAuthority('SCANNER_ADMIN') or " +
                    "hasAuthority('WAREHOUSE_MANAGER')"
    )
    public String scanSticker(@RequestParam String barcode) {

        User scanner = getCurrentUser();

        DispatchInvoice invoice =
                cageScanService.scanSticker(barcode, scanner);

        if (cageScanService.isInvoiceFullyScanned(invoice.getId())) {
            return "All stickers scanned. Invoice ready for dispatch.";
        }

        return "Sticker scanned successfully";
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }
}

