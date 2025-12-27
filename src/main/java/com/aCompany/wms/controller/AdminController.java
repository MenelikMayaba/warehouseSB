package com.aCompany.wms.controller;

import com.aCompany.wms.dto.UserDto;
import com.aCompany.wms.entity.User;
import com.aCompany.wms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;  // Changed from ch.qos.logback.core.model.Model
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String adminPanel(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        model.addAttribute("allRoles", Set.of("ADMIN", "PICKER", "PACKER", "DISPATCHER"));
        model.addAttribute("userDto", new UserDto()); // Add this line
        return "admin";
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute UserDto userDto, RedirectAttributes redirectAttributes) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Username already exists");
            return "redirect:/admin";
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRoles(userDto.getRoles());

        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "User created successfully");
        return "redirect:/admin";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        return "redirect:/admin";
    }
}