package com.aCompany.wms.controller;

import com.aCompany.wms.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.aCompany.wms.model.ActiveUser;
import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class UserSessionController {

    private final UserSessionService userSessionService;

    @Autowired
    public UserSessionController(UserSessionService userSessionService) {
        this.userSessionService = userSessionService;
    }

    /**
     * Forcefully logs out a user by their user ID
     * @return response with the number of terminated sessions
     */
    @GetMapping("/active")
    public ResponseEntity<List<ActiveUser>> getActiveUsers() {
        return ResponseEntity.ok(userSessionService.getActiveUsers());
    }

    @PostMapping("/{userId}/force-logout")
    public ResponseEntity<?> forceLogoutUser(@PathVariable Long userId) {
        try {
            int terminatedSessions = userSessionService.forceLogoutUser(userId);
            if (terminatedSessions > 0) {
                return ResponseEntity.ok().body(
                    String.format("Successfully logged out %d active session(s) for user ID: %d", 
                    terminatedSessions, userId)
                );
            }
            return ResponseEntity.ok().body("No active sessions found for user ID: " + userId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error force logging out user: " + e.getMessage());
        }
    }
}
