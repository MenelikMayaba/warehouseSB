package com.aCompany.wms.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final SessionEventListener sessionEventListener;

    public CustomAuthenticationSuccessHandler(SessionEventListener sessionEventListener) {
        this.sessionEventListener = sessionEventListener;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Get user details
        Object principal = authentication.getPrincipal();
        String username = principal instanceof org.springframework.security.core.userdetails.User
                ? ((org.springframework.security.core.userdetails.User) principal).getUsername()
                : principal.toString();

        // Get user ID (you'll need to inject UserRepository to get the ID)
        // For now, we'll use the username as a placeholder
        Long userId = getUserIdFromAuthentication(authentication);

        // Register the login with session tracking
        sessionEventListener.onAuthenticationSuccess(username, userId, request);

        // Handle role-based redirection
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                response.sendRedirect("/admin");
                return;
            }

            if (authority.getAuthority().equals("ROLE_PICKER")) {
                response.sendRedirect("/picker");
                return;
            }

            if (authority.getAuthority().equals("ROLE_PACKER")) {
                response.sendRedirect("/packer");
                return;
            }

            if (authority.getAuthority().equals("ROLE_DISPATCHER")) {
                response.sendRedirect("/dispatch");
                return;
            }
        }

        // Default redirect for other roles
        response.sendRedirect("/");
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        // You'll need to implement this method to get the actual user ID
        // For now, return a default value
        return 0L;
    }
}