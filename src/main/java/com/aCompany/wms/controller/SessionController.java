package com.aCompany.wms.controller;

import com.aCompany.wms.model.ActiveUser;
import com.aCompany.wms.service.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final UserSessionService userSessionService;

    @Autowired
    public SessionController(UserSessionService userSessionService) {
        this.userSessionService = userSessionService;
    }

    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    public List<ActiveUser> getActiveSessions() {
        return userSessionService.getActiveUsers();
    }

    @GetMapping("/active/count")
    @PreAuthorize("isAuthenticated()")
    public long getActiveSessionsCount() {
        return userSessionService.getActiveUsers().size();
    }
}