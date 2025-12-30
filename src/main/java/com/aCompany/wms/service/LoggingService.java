package com.aCompany.wms.service;

import com.aCompany.wms.model.LogEntry;
import com.aCompany.wms.repository.LogEntryRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class LoggingService {
    @Autowired
    private LogEntryRepository logEntryRepository;

    public void log(String action, String details) {
        log(action, details, null);
    }

    public void log(String action, String details, String username) {
        LogEntry logEntry = new LogEntry();
        logEntry.setAction(action);
        logEntry.setDetails(details);
        logEntry.setLevel("INFO");

        if (username == null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) {
                username = auth.getName();
            } else {
                username = "SYSTEM";
            }
        }
        logEntry.setUsername(username);

        try {
            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            logEntry.setIpAddress(getClientIP(request));
        } catch (Exception e) {
            logEntry.setIpAddress("unknown");
        }

        logEntryRepository.save(logEntry);
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
