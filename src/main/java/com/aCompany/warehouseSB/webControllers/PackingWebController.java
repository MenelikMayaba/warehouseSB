package com.aCompany.warehouseSB.webControllers;

import com.aCompany.warehouseSB.packing.PackingService;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/packing")
public class PackingWebController {

    private final PackingService packingService;

    public PackingWebController(PackingService packingService) {
        this.packingService = packingService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("readyInvoices", packingService.getReadyInvoices());
        return "packingDashboard";
    }
}
