package com.aCompany.wms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ActiveUserId implements Serializable {
    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public ActiveUserId() {}

    public ActiveUserId(String sessionId, Long userId) {
        this.sessionId = sessionId;
        this.userId = userId;
    }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActiveUserId that = (ActiveUserId) o;
        return Objects.equals(sessionId, that.sessionId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, userId);
    }
}