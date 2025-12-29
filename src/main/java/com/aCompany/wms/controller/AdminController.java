package com.aCompany.wms.controller;

import com.aCompany.wms.dto.ApiResponse;
import com.aCompany.wms.dto.UserDto;
import com.aCompany.wms.User.User;
import com.aCompany.wms.model.Item;
import com.aCompany.wms.repository.UserRepository;
import com.aCompany.wms.service.StockService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private StockService stockService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Serve the admin page
    @GetMapping
    public String adminPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            model.addAttribute("currentUser", authentication.getName());
            model.addAttribute("username", authentication.getName());
        }
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("allRoles", Set.of("ADMIN", "PICKER", "PACKER", "DISPATCHER", "RECEIVER"));
        model.addAttribute("userDto", new UserDto());
        return "admin/dashboard";
    }

    // API Endpoints
    @ResponseBody
    @GetMapping("/admin/users/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsersApi() {
        return ResponseEntity.ok(ApiResponse.success(userRepository.findAll()));
    }


    @GetMapping("/users")
    public String usersPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication.getPrincipal() instanceof String)) {
            model.addAttribute("username", authentication.getName());
        }
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("allRoles", Set.of("ADMIN", "PICKER", "PACKER", "DISPATCHER", "RECEIVER"));
        return "admin/users/users";
    }

    @GetMapping("/users/new")
    public String showAddUserForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("allRoles", Set.of("ADMIN", "PICKER", "PACKER", "DISPATCHER", "RECEIVER"));
        return "admin/users/addUser";
    }

    @ResponseBody
    @GetMapping("/api/roles")
    public ResponseEntity<ApiResponse<Set<String>>> getAvailableRolesApi() {
        return ResponseEntity.ok(ApiResponse.success(Set.of("ADMIN", "PICKER", "PACKER", "DISPATCHER", "RECEIVER")));
    }


    @PostMapping("/users")
    public String createUser(@Valid UserDto userDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", Set.of("ADMIN", "PICKER", "PACKER", "DISPATCHER", "RECEIVER"));
            return "admin/users/addUser";
        }

        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            model.addAttribute("error", "Username already exists");
            model.addAttribute("allRoles", Set.of("ADMIN", "PICKER", "PACKER", "DISPATCHER", "RECEIVER"));
            return "admin/users/addUser";
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRoles(userDto.getRoles());
        userRepository.save(user);

        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }

    @ResponseBody
    @GetMapping("/admin/stockView")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Item>>> getAllStockApi() {
        return ResponseEntity.ok(ApiResponse.success(stockService.getAllStock()));
    }

}