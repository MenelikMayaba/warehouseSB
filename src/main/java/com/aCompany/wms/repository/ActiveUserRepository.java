package com.aCompany.wms.repository;

import com.aCompany.wms.model.ActiveUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActiveUserRepository extends JpaRepository<ActiveUser, String> {


    @Query("SELECT au FROM ActiveUser au WHERE au.id.userId = :userId")
    List<ActiveUser> findByUserId(@Param("userId") Long userId);

    @Query("SELECT au FROM ActiveUser au WHERE au.id.sessionId = :sessionId")
    Optional<ActiveUser> findBySessionId(@Param("sessionId") String sessionId);

    @Modifying
    @Query("DELETE FROM ActiveUser au WHERE au.id.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM ActiveUser au WHERE au.id.sessionId = :sessionId")
    void deleteBySessionId(@Param("sessionId") String sessionId);

    @Query("SELECT au FROM ActiveUser au ORDER BY au.loginTime DESC")
    List<ActiveUser> findAllByOrderByLoginTimeDesc();

    @Modifying
    @Query("DELETE FROM ActiveUser au WHERE au.lastActivity < :cutoffTime")
    void deleteInactiveSessions(@Param("cutoffTime") LocalDateTime cutoffTime);

    @Query("SELECT CASE WHEN COUNT(au) > 0 THEN true ELSE false END FROM ActiveUser au WHERE au.id.userId = :userId")
    boolean existsByUserId(@Param("userId") Long userId);
}