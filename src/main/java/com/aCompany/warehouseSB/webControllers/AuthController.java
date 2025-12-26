package com.aCompany.warehouseSB.webControllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "/login";
    }

    @GetMapping("/post-login")
    public String postLogin(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login?error";
        }

        if (auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_WAREHOUSE_MANAGER"))) {
            return "redirect:/adminDashboard";
        }

        if (auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PICKER"))) {
            return "redirect:/pickerDashboard";
        }

        if (auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PACKER"))) {
            return "redirect:/packingDashboard";
        }

        if (auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SCANNER_ADMIN"))) {
            return "redirect:/scannerDashboard";
        }

        return "redirect:/login?error";
    }
}
