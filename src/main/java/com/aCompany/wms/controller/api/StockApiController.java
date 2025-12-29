package com.aCompany.wms.controller.api;

import com.aCompany.wms.dto.ApiResponse;
import com.aCompany.wms.model.Item;
import com.aCompany.wms.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockApiController {

    private final StockService stockService;

    @Autowired
    public StockApiController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Item>>> getAllStock() {
        List<Item> allStock = stockService.getAllStock();
        return ResponseEntity.ok(ApiResponse.success(allStock));
    }
}
