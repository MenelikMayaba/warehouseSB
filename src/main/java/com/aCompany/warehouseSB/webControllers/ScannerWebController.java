package com.aCompany.warehouseSB.webControllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/scanner")
public class ScannerWebController {

    @GetMapping
    public String dashboard() {
        return "scannerDashboard";
    }
}
