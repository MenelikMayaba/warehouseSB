package com.aCompany.warehouseSB.webControllers;

import com.aCompany.warehouseSB.packing.PackingService;
import com.aCompany.warehouseSB.user.User;
import com.aCompany.warehouseSB.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_WAREHOUSE_MANAGER')")
public class AdminWebController {

    private final UserService userService;
    private static final int DEFAULT_PAGE_SIZE = 10;
    PackingService packingService;

    public AdminWebController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "/adminDashboard";
    }

    @GetMapping("/users")
    public String manageUsers() {
        return "admin/users";
    }

    @GetMapping("/users/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "admin/users/edit";
    }

    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user, RedirectAttributes redirectAttributes) {
        userService.updateUser(id, user);
        redirectAttributes.addFlashAttribute("successMessage", "User updated successfully");
        return "redirect:/admin/users";
    }

    @GetMapping("/settings")
    public String settings() {
        return "adminSettings";
    }

    @GetMapping("/logs")
    public String viewLogs() {
        return "adminLogs";
    }

    @PostMapping("/complete-packing/{orderId}")
    @ResponseBody
    public ResponseEntity<?> completePacking(@PathVariable Long orderId) {
        try {

            packingService.completePackingOrder(orderId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/cancel-packing/{orderId}")
    @ResponseBody
    public ResponseEntity<?> cancelPacking(@PathVariable Long orderId) {
        try {
            packingService.cancelPackingOrder(orderId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
