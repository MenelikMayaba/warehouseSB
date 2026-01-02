package com.aCompany.wms.controller;

import com.aCompany.wms.entity.Location;
import com.aCompany.wms.entity.LocationType;
import com.aCompany.wms.exceptions.ResourceNotFoundException;
import com.aCompany.wms.service.LocationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationController {
    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<Page<Location>> getAllLocations(Pageable pageable) {
        return ResponseEntity.ok((Page<Location>) locationService.getAllLocations(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable Long id) throws ResourceNotFoundException {
        return ResponseEntity.ok(locationService.getLocationById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Location> getLocationByCode(@PathVariable String code) throws ResourceNotFoundException {
        return ResponseEntity.ok(locationService.getLocationByCode(code));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Location>> getLocationsByType(@PathVariable LocationType type) {
        return ResponseEntity.ok(locationService.getLocationsByType(type));
    }

    @PostMapping
    public ResponseEntity<Location> createLocation(@RequestBody Location location) {
        return ResponseEntity.ok(locationService.createLocation(location));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Location> updateLocation(
            @PathVariable Long id,
            @RequestBody Location locationDetails) throws ResourceNotFoundException {
        return ResponseEntity.ok(locationService.updateLocation(id, locationDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) throws ResourceNotFoundException {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
