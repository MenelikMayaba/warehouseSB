package com.aCompany.wms.repository;

import com.aCompany.wms.entity.Location;
import com.aCompany.wms.entity.LocationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByCode(String code);
    List<Location> findByType(LocationType type);
    List<Location> findByActive(boolean active);

    boolean existsByCode(String code);
}