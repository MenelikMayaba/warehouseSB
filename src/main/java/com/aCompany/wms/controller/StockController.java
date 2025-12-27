package com.aCompany.wms.controller;

import com.aCompany.wms.repository.StockRepository;
import org.springframework.ui.Model;  // Add this line
import com.aCompany.wms.model.Item;
import com.aCompany.wms.model.StockLocation;
import com.aCompany.wms.repository.StockLocationRepository;
import com.aCompany.wms.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping
    public String viewStockPage(Model model) {
        List<Item> optimizedItems = stockService.getOptimizedStorage("HIGH_DEMAND");
        model.addAttribute("items", optimizedItems);
        return "stock";
    }

    @PostMapping("/putaway/{id}/{locationId}")
    public String putAwayItem(@PathVariable Long id, @PathVariable Long locationId, 
                            @Autowired StockRepository stockRepo, 
                            @Autowired StockLocationRepository locationRepo) {
        Item item = stockRepo.findById(id).orElseThrow();
        StockLocation location = locationRepo.findById(locationId).orElseThrow();
        stockService.putAway(item, location);
        return "redirect:/stock";
    }
}

