package com.aCompany.warehouseSB.picking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PickRepository extends JpaRepository<Pick, Long> {
    long countByPickerIsNotNullAndStatus(PickStatus status);

    @Query("SELECT COUNT(p) FROM Pick p WHERE FUNCTION('DATE', p.pickedAt) = CURRENT_DATE")
    long countPickedToday();

    @Query("SELECT AVG(TIMESTAMPDIFF(SECOND, p.startedAt, p.completedAt)) FROM Pick p WHERE p.completedAt IS NOT NULL")
    Optional<Double> findAveragePickTime();

    @Query("SELECT (COUNT(p) * 100.0 / (SELECT COUNT(p2) FROM Pick p2)) FROM Pick p WHERE p.status = 'COMPLETED'")
    Optional<Double> findPickingAccuracy();

    int countByCompletedAtAfterAndStatus(LocalDateTime startOfDay, PickStatus status);

    @Query("SELECT COUNT(p) FROM Pick p WHERE p.picker IS NOT NULL AND p.status = :status")
    long countByPickerNotNull(@Param("status") PickStatus status);
}