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
import java.util.Map;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final SessionEventListener sessionEventListener;

    public CustomAuthenticationSuccessHandler(SessionEventListener sessionEventListener) {
        this.sessionEventListener = sessionEventListener;
    }

    private static final Map<String, String> ROLE_REDIRECTS = Map.of(
        "ROLE_ADMIN", "/admin",
        "ROLE_RECEIVER", "/receiving",
        "ROLE_PICKER", "/picker",
        "ROLE_PACKER", "/packer",
        "ROLE_DISPATCHER", "/dispatch"
    );

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        handleUserSession(authentication, request);
        redirectBasedOnRole(authentication, response);
    }

    private void handleUserSession(Authentication authentication, HttpServletRequest request) {
        String username = extractUsername(authentication);
        Long userId = getUserIdFromAuthentication(authentication);
        sessionEventListener.onAuthenticationSuccess(username, userId, request);
    }

    private String extractUsername(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        return principal instanceof org.springframework.security.core.userdetails.User
                ? ((org.springframework.security.core.userdetails.User) principal).getUsername()
                : principal.toString();
    }

    private void redirectBasedOnRole(Authentication authentication, HttpServletResponse response) throws IOException {
        String redirectUrl = findMatchingRedirectUrl(authentication.getAuthorities());
        response.sendRedirect(redirectUrl);
    }

    private String findMatchingRedirectUrl(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(ROLE_REDIRECTS::containsKey)
                .findFirst()
                .map(ROLE_REDIRECTS::get)
                .orElse("/");
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        // You'll need to implement this method to get the actual user ID
        // For now, return a default value
        return 0L;
    }
}