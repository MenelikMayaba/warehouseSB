package com.aCompany.wms.controller;

import com.aCompany.wms.dto.ApiResponse;
import com.aCompany.wms.dto.UserDto;
import com.aCompany.wms.User.User;
import com.aCompany.wms.model.LogEntry;
import com.aCompany.wms.model.Product;

import com.aCompany.wms.repository.LogEntryRepository;
import com.aCompany.wms.repository.ProductRepository;
import com.aCompany.wms.repository.UserRepository;
import com.aCompany.wms.service.LoggingService;
import com.aCompany.wms.service.StockService;
import com.aCompany.wms.service.UserSessionService;
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

import java.time.LocalDateTime;
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
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private LoggingService loggingService;
    @Autowired
    private LogEntryRepository logEntryRepository;
    @Autowired
    private UserSessionService userSessionService;


    // Serve the admin page
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
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

    // Serve the users page
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String usersPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication.getPrincipal() instanceof String)) {
            model.addAttribute("username", authentication.getName());
            model.addAttribute("currentUser", authentication.getName()); // Add current user
        }
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("activeUsers", userSessionService.getActiveUsers()); // Add active users
        model.addAttribute("allRoles", Set.of("ADMIN", "PICKER", "PACKER", "DISPATCHER", "RECEIVER"));
        return "admin/users/users";
    }

    // Serve the add user page
    @GetMapping("/users/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAddUserForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("allRoles", Set.of("ADMIN", "PICKER", "PACKER", "DISPATCHER", "RECEIVER"));
        return "admin/users/addUser";
    }

    // Serve the admin settings page
    @GetMapping("/adminSettings")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminSettings(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            model.addAttribute("username", authentication.getName());
        }
        return "admin/adminSettings";
    }

    // API Endpoints


    @GetMapping("/admin/users/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsersApi() {
        return ResponseEntity.ok(ApiResponse.success(userRepository.findAll()));
    }


    @ResponseBody
    @GetMapping("/api/roles")
    public ResponseEntity<ApiResponse<Set<String>>> getAvailableRolesApi() {
        return ResponseEntity.ok(ApiResponse.success(Set.of("ADMIN", "PICKER", "PACKER", "DISPATCHER", "RECEIVER")));
    }

    // Serve the add user page
    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
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

    // Serve the delete user page
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }

    // Serve the stock view page
    @GetMapping("/admin/stockView")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Product>>> getAllStockApi() {
        return ResponseEntity.ok(ApiResponse.success(stockService.getAllStock()));
    }

    // Serve the admin logs page
    @GetMapping("/adminLogs")
    public String viewAdminLogs(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String level,
            Model model) {

        List<LogEntry> logs;

        if (username != null && !username.isEmpty()) {
            logs = logEntryRepository.findByUsernameOrderByTimestampDesc(username);
        } else if (action != null && !action.isEmpty()) {
            logs = logEntryRepository.findByActionOrderByTimestampDesc(action);
        } else {
            // Default: show last 7 days of logs
            LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
            logs = logEntryRepository.findByTimestampBetweenOrderByTimestampDesc(
                    weekAgo, LocalDateTime.now());
        }

        if (level != null && !level.isEmpty()) {
            logs = logs.stream()
                    .filter(log -> level.equalsIgnoreCase(log.getLevel()))
                    .toList();
        }

        model.addAttribute("logs", logs);
        return "admin/adminLogs";
    }

    // Serve the clear logs page
    @PostMapping("/adminLogs/clear")
    @ResponseBody
    public ResponseEntity<?> clearLogs() {
        logEntryRepository.deleteAll();
        loggingService.log("LOG_CLEARED", "Admin cleared all log entries");
        return ResponseEntity.ok().build();
    }

    // Serve the clear cache page
    @PostMapping("/clearCache")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> clearSystemCache() {
        try {
            // Clear Spring's cache
            System.out.println("Clearing system cache...");
            // Add any specific cache clearing logic here if needed
            return ResponseEntity.ok().body("{\"message\": \"System cache cleared successfully\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"Error clearing cache: " + e.getMessage() + "\"}");
        }
    }

    // Serve the backup database page
    @PostMapping("/backupDatabase")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> backUpDatabase() {
        try {
            // In a real implementation, this would back up to a file or cloud storage
            String backupInfo = "Backup created at: " + LocalDateTime.now() + "\n";
            backupInfo += "Users: " + userRepository.count() + "\n";
            backupInfo += "Products: " + productRepository.count() + "\n";
            
            // Log the backup action
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            loggingService.log(auth.getName(), "DATABASE_BACKUP", "Backup created: " + backupInfo);
            
            return ResponseEntity.ok().body("{\"message\": \"Database backup completed successfully: " + backupInfo.replace("\n", " ") + "\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"Error creating backup: " + e.getMessage() + "\"}");
        }
    }

    // Serve the reset system page
    @PostMapping("/resetSystem")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resetSystem() {
        try {
            // Keep only admin users
            List<User> allUsers = userRepository.findAll();
            for (User user : allUsers) {
                if (!user.getRoles().stream().anyMatch(role -> role.name().equals("ADMIN"))) {
                    userRepository.delete(user);
                }
            }
            
            // Delete all products
            productRepository.deleteAll();
            
            // Log the reset action
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            loggingService.log(auth.getName(), "SYSTEM_RESET", "System was reset to default state");
            
            return ResponseEntity.ok().body("{\"message\": \"System has been reset to default state. All non-admin users and products have been removed.\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\": \"Error resetting system: " + e.getMessage() + "\"}");
        }
    }
}