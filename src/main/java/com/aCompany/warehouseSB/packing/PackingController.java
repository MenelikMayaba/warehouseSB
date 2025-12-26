package com.aCompany.warehouseSB.packing;

import com.aCompany.warehouseSB.user.User;
import com.aCompany.warehouseSB.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/packing")
public class PackingController {

    private final PackingService packingService;

    public PackingController(PackingService packingService) {
        this.packingService = packingService;
    }

    @PostMapping("/pack")
    @PreAuthorize("hasAnyAuthority('PACKER', 'PICKER_PACKER_ADMIN', 'WAREHOUSE_MANAGER')")
    public DispatchInvoice packInvoice(@RequestParam Long invoiceId) {
        User packer = getCurrentUser();
        return packingService.packInvoice(invoiceId, packer);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }


}

