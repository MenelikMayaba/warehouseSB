package com.aCompany.wms.config;

import com.aCompany.wms.service.UserSessionService;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SessionEventListener implements HttpSessionListener {

    private final UserSessionService userSessionService;

    @Autowired
    public SessionEventListener(@Lazy UserSessionService userSessionService) {
        this.userSessionService = userSessionService;
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        // Session creation is handled by the authentication success handler
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        String sessionId = se.getSession().getId();
        userSessionService.registerLogout(sessionId);
    }

    public void onAuthenticationSuccess(String username, Long userId, jakarta.servlet.http.HttpServletRequest request) {
        userSessionService.registerLogin(request, userId, username);
    }
}
