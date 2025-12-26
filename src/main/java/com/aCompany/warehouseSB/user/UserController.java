package com.aCompany.warehouseSB.user;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ADMIN-ONLY (for now by convention)
    @PostMapping
    public User createUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam Role role
    ) {
        return userService.createUser(username, password, role);
    }

    // ADMIN-ONLY (view all users)
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // ADMIN-ONLY (view user by ID)
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }


}
