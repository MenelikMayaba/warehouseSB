package com.aCompany.wms.repository;

import com.aCompany.wms.model.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    List<LogEntry> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime start, LocalDateTime end);
    List<LogEntry> findByUsernameOrderByTimestampDesc(String username);
    List<LogEntry> findByActionOrderByTimestampDesc(String action);
}
