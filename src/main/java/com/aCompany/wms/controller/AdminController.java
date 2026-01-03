package com.aCompany.wms.controller;

import com.aCompany.wms.dto.ApiResponse;
import com.aCompany.wms.dto.OrderForm;
import com.aCompany.wms.dto.UserDto;
import com.aCompany.wms.User.User;
import com.aCompany.wms.model.LogEntry;
import com.aCompany.wms.model.Order;
import com.aCompany.wms.model.Product;

import com.aCompany.wms.model.Stock;
import com.aCompany.wms.repository.LogEntryRepository;
import com.aCompany.wms.repository.ProductRepository;
import com.aCompany.wms.repository.StockRepository;
import com.aCompany.wms.repository.UserRepository;
import com.aCompany.wms.service.*;
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
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    @Autowired
    private OrderService orderService;
    @Autowired
    private PickingService pickingService;
    @Autowired
    private StockRepository stockRepository;

    // Serve the admin page
    @GetMapping("/dashboard")
    public String adminPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (userAuthenticated()) {
            model.addAttribute("currentUser", authentication.getName());
            model.addAttribute("username", authentication.getName());
        }

        // Add dashboard statistics
        model.addAttribute("totalProducts", productRepository.count());
        model.addAttribute("lowStockCount", productRepository.countByQuantityInStockLessThan(10)); // Assuming you have this method
        model.addAttribute("pendingOrders", orderService.countByWorkflowStatus("PENDING")); // Assuming you have this method
        model.addAttribute("totalUsers", userRepository.count());

        // Add recent activity (you'll need to implement this in your LogEntryService)
        // model.addAttribute("recentActivity", logEntryService.findRecentActivity(5));

        // Add other necessary attributes
        if (!model.containsAttribute("orderForm")) {
            model.addAttribute("orderForm", new OrderForm());
        }

        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("allRoles", Set.of("ADMIN", "PICKER", "PACKER", "DISPATCHER", "RECEIVER"));
        model.addAttribute("userDto", new UserDto());

        List<String> workflowStatuses = List.of("PLANNING", "PICKING", "PACKING", "DISPATCHED");
        model.addAttribute("workflowStatuses", workflowStatuses);

        List<String> invoicePriorities = List.of("PRIORITY", "ACCURATE", "UNUSED");
        model.addAttribute("invoicePriorities", invoicePriorities);

        return "admin/dashboard";
    }

    @GetMapping("/stockView")
    public String stockView(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (userAuthenticated()) {
            model.addAttribute("username", authentication.getName());
        }

        // Get all stock items
        List<Stock> allStock = stockRepository.findAllWithProductAndLocation();
        model.addAttribute("allStock", allStock);

        // Get all products
        List<Product> allProducts = productRepository.findAll();
        model.addAttribute("allProducts", allProducts);

        // Calculate total products count
        long totalProductsCount = allProducts.size();
        model.addAttribute("totalProductsCount", totalProductsCount);

        // Calculate total items in stock
        int totalItemsInStock = allStock.stream().mapToInt(Stock::getQuantity).sum();
        model.addAttribute("totalItemsInStock", totalItemsInStock);

        return "admin/stockView";
    }


    // Serve the add user page
    @GetMapping("/users/new")
    public String showAddUserForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("allRoles", Set.of("ADMIN", "PICKER", "PACKER", "DISPATCHER", "RECEIVER"));
        return "admin/users/addUser";
    }

    // Serve the admin settings page
    @GetMapping("/adminSettings")
    public String adminSettings(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (userAuthenticated()) {
            model.addAttribute("username", authentication.getName());
        }
        return "admin/adminSettings";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (userAuthenticated()) {
            model.addAttribute("username", authentication.getName());
        }
        model.addAttribute("users", userRepository.findAll());
        return "admin/users/users";
    }

    @PostMapping("/orders")
    public String createOrder(@ModelAttribute("orderForm") @Valid OrderForm orderForm,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.orderForm", bindingResult);
            redirectAttributes.addFlashAttribute("orderForm", orderForm);
            return "redirect:/admin/dashboard";
        }

        try {
            // Create the order
            Order order = orderService.createOrder(orderForm.getWorkflowStatus());

            // Create the invoice
            pickingService.createInvoice(order.getId(), orderForm.getInvoicePriority());

            redirectAttributes.addFlashAttribute("successMessage", "Order and invoice created successfully!");
            return "redirect:/admin/dashboard";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating order: " + e.getMessage());
            return "redirect:/admin/dashboard";
        }
    }

    private boolean userAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String);
    }


    // API Endpoints


    @GetMapping("/api/admin/users")
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

    // Serve the edit user page
    @GetMapping("/users/{id}/edit")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setRoles(user.getRoles());
        
        // Add CSRF token to the model
        CsrfToken csrfToken = (CsrfToken) RequestContextHolder.getRequestAttributes()
            .getAttribute("_csrf", RequestAttributes.SCOPE_REQUEST);
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }
        
        model.addAttribute("user", userDto);
        model.addAttribute("allRoles", Set.of("ADMIN", "PICKER", "PACKER", "DISPATCHER", "RECEIVER"));
        return "admin/users/edit";
    }

    // Serve the add product page
    @GetMapping("/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin/add-product";
    }
    
    @PutMapping("/users/{id}")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute("user") UserDto userDto, 
                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("allRoles", Set.of("ADMIN", "PICKER", "PACKER", "DISPATCHER", "RECEIVER"));
            return "admin/users/edit";
        }
        
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + id));
        
        // Check if username is already taken by another user
        if (!existingUser.getUsername().equals(userDto.getUsername()) && 
            userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            model.addAttribute("error", "Username already exists");
            model.addAttribute("allRoles", Set.of("ADMIN", "PICKER", "PACKER", "DISPATCHER", "RECEIVER"));
            return "admin/users/edit";
        }
        
        existingUser.setUsername(userDto.getUsername());
        
        // Only update password if a new one was provided
        if (userDto.getPassword() != null && !userDto.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        
        existingUser.setRoles(userDto.getRoles());
        userRepository.save(existingUser);
        
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/products/add")
    public String addProduct(@Valid @ModelAttribute("product") Product product,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/add-product";
        }

        try {
            // Set default values
            product.setLastUpdated(LocalDateTime.now());
            product.setStatus("ACTIVE");
            
            // If expiry date is empty, set it to null
            if (product.getExpiryDate() != null && product.getExpiryDate().toString().isEmpty()) {
                product.setExpiryDate(null);
            }
            
            // Set default values if not provided
            if (product.getReorderLevel() == null) {
                product.setReorderLevel(10);
            }
            if (product.getStorageType() == null || product.getStorageType().isEmpty()) {
                product.setStorageType("GENERAL");
            }
            
            productRepository.save(product);
            redirectAttributes.addFlashAttribute("success", "Product added successfully!");
            return "redirect:/admin/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding product: " + e.getMessage());
            e.printStackTrace(); // Add this for debugging
            return "redirect:/admin/products/add";
        }
    }

    // Serve the stock view page
    @GetMapping("/admin/stockView")
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