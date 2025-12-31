package com.aCompany.wms.service;

import com.aCompany.wms.model.ActiveUserId;
import com.aCompany.wms.model.ActiveUser;
import com.aCompany.wms.repository.ActiveUserRepository;
import jakarta.annotation.PreDestroy;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserSessionService {
    private static final Logger logger = LoggerFactory.getLogger(UserSessionService.class);
    @Autowired
    private ActiveUserRepository activeUserRepository;

    @Transactional
    public void registerLogin(HttpServletRequest request, Long userId, String username) {
        String sessionId = request.getSession().getId();
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        LocalDateTime now = LocalDateTime.now();

        // Log the new session
        logger.info("Registering new session - User: {}, Session ID: {}, IP: {}",
                username, sessionId, ipAddress);

        ActiveUser activeUser = new ActiveUser();
        activeUser.setSessionId(sessionId);
        activeUser.setUserId(userId);
        activeUser.setUsername(username);
        activeUser.setIpAddress(ipAddress);
        activeUser.setUserAgent(userAgent);
        activeUser.setLoginTime(now);
        activeUser.setLastActivity(now);

        activeUserRepository.save(activeUser);

        // Log all active sessions
        List<ActiveUser> allSessions = activeUserRepository.findAll();
        logger.info("Current active sessions: {}", allSessions.size());
        allSessions.forEach(session ->
                logger.info("Active session - User: {}, Session ID: {}, Last Active: {}",
                        session.getUsername(), session.getSessionId(), session.getLastActivity())
        );
    }

    @Transactional(readOnly = true)
    public List<ActiveUser> getActiveUsers() {
        List<ActiveUser> activeUsers = activeUserRepository.findAllByOrderByLoginTimeDesc();
        logger.info("Retrieving {} active sessions", activeUsers.size());
        return activeUsers;
    }

    @Transactional
    public void updateLastActivity(String sessionId) {
        ActiveUser activeUser = activeUserRepository.findBySessionId(sessionId);
        if (activeUser != null) {
            activeUser.updateLastActivity();
            activeUserRepository.save(activeUser);
        }
    }

    @Transactional
    public void registerLogout(String sessionId) {
        activeUserRepository.deleteBySessionId(sessionId);
    }

    public boolean isUserActive(Long userId) {
        return activeUserRepository.existsByUserId(userId);
    }


    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    @Transactional
    public void cleanInactiveSessions() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(30); // 30 minutes of inactivity
        activeUserRepository.deleteInactiveSessions(cutoffTime);
    }


    /**
     * Forcefully logs out a user by their user ID
     * @param userId the ID of the user to log out
     * @return the number of active sessions that were terminated
     */
    @Transactional
    public int forceLogoutUser(Long userId) {
        try {
            List<ActiveUser> activeSessions = activeUserRepository.findByUserId(userId);
            if (activeSessions != null && !activeSessions.isEmpty()) {
                int count = activeSessions.size();
                activeUserRepository.deleteByUserId(userId);
                logger.info("Forcefully logged out user with ID: {}. {} sessions terminated.", userId, count);
                return count;
            }
            logger.info("No active sessions found for user ID: {}", userId);
            return 0;
        } catch (Exception e) {
            logger.error("Error force logging out user with ID: " + userId, e);
            throw new RuntimeException("Failed to force logout user", e);
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    @PreDestroy
    @Transactional
    public void onShutdown() {
        try {
            long count = activeUserRepository.count();
            activeUserRepository.deleteAll();
            logger.info("Cleared {} active sessions during server shutdown", count);
        } catch (Exception e) {
            logger.error("Error clearing active sessions during shutdown", e);
        }
    }
}