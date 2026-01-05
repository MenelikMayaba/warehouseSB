package com.aCompany.wms.config;

import com.aCompany.wms.entity.Location;
import com.aCompany.wms.entity.LocationType;
import com.aCompany.wms.repository.LocationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class LocationDataInitializer implements CommandLineRunner {

    private final LocationRepository locationRepository;

    public LocationDataInitializer(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public void run(String... args) {
        // Create receiving location if it doesn't exist
        if (locationRepository.findByCode("RECEIVING").isEmpty()) {
            Location receiving = new Location();
            receiving.setCode("RECEIVING");
            receiving.setName("Receiving Area");
            receiving.setType(LocationType.RECEIVING);
            receiving.setDescription("Temporary storage for received items before put-away");
            locationRepository.save(receiving);
        }

        // Create warehouse locations if they don't exist
        createWarehouseLocationIfNotExists("GEN-001", "General Storage", LocationType.STORAGE, "General storage area");
        createWarehouseLocationIfNotExists("COLD-001", "Refrigerated Storage", LocationType.COLD_STORAGE, "Refrigerated storage area");
        createWarehouseLocationIfNotExists("FROZEN-001", "Frozen Storage", LocationType.COLD_STORAGE, "Frozen storage area");
        createWarehouseLocationIfNotExists("HAZ-001", "Hazardous Storage", LocationType.STORAGE, "Hazardous materials storage");
    }

    private void createWarehouseLocationIfNotExists(String code, String name, LocationType type, String description) {
        if (locationRepository.findByCode(code).isEmpty()) {
            Location location = new Location();
            location.setCode(code);
            location.setName(name);
            location.setType(type);
            location.setDescription(description);
            locationRepository.save(location);
        }
    }
}