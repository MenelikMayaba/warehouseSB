package com.aCompany.wms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "active_users")
public class ActiveUser {
    @EmbeddedId
    private ActiveUserId id = new ActiveUserId();

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "login_time", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime loginTime;

    @Column(name = "last_activity", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime lastActivity;

    // Getters and setters for the composite ID fields
    @Transient
    public String getSessionId() {
        return id != null ? id.getSessionId() : null;
    }

    public void setSessionId(String sessionId) {
        if (id == null) {
            id = new ActiveUserId();
        }
        id.setSessionId(sessionId);
    }

    @Transient
    public Long getUserId() {
        return id != null ? id.getUserId() : null;
    }

    public void setUserId(Long userId) {
        if (id == null) {
            id = new ActiveUserId();
        }
        id.setUserId(userId);
    }

    // Standard getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public LocalDateTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }

    public LocalDateTime getLastActivity() { return lastActivity; }
    public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }

    public void updateLastActivity() {
        this.lastActivity = LocalDateTime.now();
    }
}