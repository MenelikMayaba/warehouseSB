package com.aCompany.warehouseSB.picking;

import com.aCompany.warehouseSB.user.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/picking")
public class PickingController {

    private final PickingService pickingService;

    public PickingController(PickingService pickingService) {
        this.pickingService = pickingService;
    }

    @PostMapping("/pick")
    @PreAuthorize("hasAnyAuthority('PICKER', 'PICKER_PACKER_ADMIN', 'WAREHOUSE_MANAGER')")
    public String pickInvoice(@RequestParam Long invoiceId, Long testPickerId) {
        if (invoiceId == null) {
            throw new IllegalArgumentException("Invoice ID cannot be null");
        }
        User picker = getCurrentUser();
        pickingService.pickInvoice(invoiceId, picker);

        return "Invoice picked successfully";
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }
}
