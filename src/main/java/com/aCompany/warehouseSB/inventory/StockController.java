package com.aCompany.warehouseSB.inventory;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class StockController {

    private final ItemService itemService;
    private final StockService stockService;

    public StockController(ItemService itemService, StockService stockService) {
        this.itemService = itemService;
        this.stockService = stockService;
    }

    @PostMapping("/items")
    @PreAuthorize("hasAuthority('WAREHOUSE_MANAGER')")
    public Item createItem(@RequestParam String name, @RequestParam String sku) {
        return itemService.createItem(name, sku);
    }

    @GetMapping("/items")
    @PreAuthorize("hasAuthority('WAREHOUSE_MANAGER')")
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    @PostMapping("/stock/add")
    @PreAuthorize("hasAuthority('WAREHOUSE_MANAGER')")
    public void addStock(@RequestParam Long itemId, @RequestParam int quantity) {
        Item item = itemService.getItemById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        stockService.addStock(item, quantity);
    }

    @PostMapping("/stock/remove")
    @PreAuthorize("hasAuthority('WAREHOUSE_MANAGER')")
    public void removeStock(@RequestParam Long itemId, @RequestParam int quantity) {
        Item item = itemService.getItemById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        stockService.removeStock(item, quantity);
    }

    @GetMapping("/stock")
    @PreAuthorize("hasAuthority('WAREHOUSE_MANAGER')")
    public List<Stock> viewStock() {
        return stockService.getAllStock();
    }

    @GetMapping("/stock/levels")
    @PreAuthorize("hasAuthority('WAREHOUSE_MANAGER')")
    public Map<String, Integer> getStockLevelsMap() {
        return stockService.getStockLevelsMap();
    }
}

