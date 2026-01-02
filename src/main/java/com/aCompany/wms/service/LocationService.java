package com.aCompany.wms.service;

import com.aCompany.wms.entity.Location;
import com.aCompany.wms.entity.LocationType;
import com.aCompany.wms.exceptions.ResourceNotFoundException;
import com.aCompany.wms.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;

    public List<Location> getAllLocations(Pageable pageable) {
        return locationRepository.findAll();
    }

    public Location getLocationById(Long id) throws ResourceNotFoundException {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + id));
    }

    public Location getLocationByCode(String code) throws ResourceNotFoundException {
        return locationRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with code: " + code));
    }

    public List<Location> getLocationsByType(LocationType type) {
        return locationRepository.findByType(type);
    }

    @Transactional
    public Location createLocation(Location location) {
        if (locationRepository.existsByCode(location.getCode())) {
            throw new IllegalArgumentException("Location with code " + location.getCode() + " already exists");
        }
        return locationRepository.save(location);
    }

    @Transactional
    public Location updateLocation(Long id, Location locationDetails) throws ResourceNotFoundException {
        Location location = getLocationById(id);

        if (!location.getCode().equals(locationDetails.getCode()) &&
                locationRepository.existsByCode(locationDetails.getCode())) {
            throw new IllegalArgumentException("Location with code " + locationDetails.getCode() + " already exists");
        }

        location.setCode(locationDetails.getCode());
        location.setName(locationDetails.getName());
        location.setType(locationDetails.getType());
        location.setActive(locationDetails.isActive());

        return locationRepository.save(location);
    }

    @Transactional
    public void deleteLocation(Long id) throws ResourceNotFoundException {
        Location location = getLocationById(id);
        // Add validation if location is in use
        locationRepository.delete(location);
    }
}
