package com.aCompany.wms.service;

import com.aCompany.wms.entity.Inventory;
import com.aCompany.wms.entity.Location;
import com.aCompany.wms.model.Product;
import com.aCompany.wms.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final InventoryRepository inventoryRepository;

    public Map<String, Object> getInventorySummary() {
        Map<String, Object> summary = new HashMap<>();

        // Total products in stock
        long totalProducts = inventoryRepository.count();
        summary.put("totalProducts", totalProducts);

        // Total inventory value
        BigDecimal totalValue = inventoryRepository.calculateTotalInventoryValue()
                .orElse(BigDecimal.ZERO);
        summary.put("totalInventoryValue", totalValue);

        // Low stock items
        List<Inventory> lowStockItems = inventoryRepository.findLowStockItems(10); // Threshold of 10
        summary.put("lowStockItems", lowStockItems);
        summary.put("lowStockCount", lowStockItems.size());

        // Inventory by location
        Map<String, Long> inventoryByLocation = new HashMap<>();
        List<Object[]> locationStats = inventoryRepository.getInventoryCountByLocation();
        for (Object[] stat : locationStats) {
            inventoryByLocation.put((String) stat[0], (Long) stat[1]);
        }
        summary.put("inventoryByLocation", inventoryByLocation);

        return summary;
    }

    public Map<String, Object> getInventoryValuationReport() {
        Map<String, Object> report = new HashMap<>();

        // Total inventory value
        BigDecimal totalValue = inventoryRepository.calculateTotalInventoryValue()
                .orElse(BigDecimal.ZERO);
        report.put("totalInventoryValue", totalValue);

        // Value by product category
        List<Object[]> valueByCategory = inventoryRepository.getInventoryValueByCategory();
        report.put("valueByCategory", valueByCategory);

        // Top 10 most valuable items
        List<Object[]> topItems = inventoryRepository.getTopValuableItems(10);
        report.put("topValuableItems", topItems);

        // Inventory aging
        List<Object[]> agingReport = inventoryRepository.getInventoryAgingReport();
        report.put("inventoryAging", agingReport);

        return report;
    }

    public Map<String, Object> getMovementReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> report = new HashMap<>();

        // Total movements
        long totalMovements = inventoryRepository.countMovementsInDateRange(startDate, endDate);
        report.put("totalMovements", totalMovements);

        // Movements by type
        List<Object[]> movementsByType = inventoryRepository.getMovementCountsByType(startDate, endDate);
        report.put("movementsByType", movementsByType);

        // Busiest locations with pagination
        Page<Object[]> busiestLocationsPage = inventoryRepository.getBusiestLocations(
                startDate,
                endDate,
                PageRequest.of(0, 10)  // First page, 10 items per page
        );
        report.put("busiestLocations", busiestLocationsPage.getContent());
        report.put("totalBusyLocations", busiestLocationsPage.getTotalElements());

        // Most moved products
        List<Object[]> mostMovedProducts = inventoryRepository.getMostMovedProducts(startDate, endDate, 10);
        report.put("mostMovedProducts", mostMovedProducts);

        return report;
    }

}