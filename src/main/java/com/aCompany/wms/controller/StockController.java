package com.aCompany.wms.controller;

import com.aCompany.wms.dto.ApiResponse;
import com.aCompany.wms.exceptions.LocationNotFoundException;
import com.aCompany.wms.exceptions.ProductNotFoundException;
import com.aCompany.wms.exceptions.StockNotFoundException;
import com.aCompany.wms.model.Product;
import com.aCompany.wms.model.Stock;
import com.aCompany.wms.model.StockLocation;
import com.aCompany.wms.repository.ProductRepository;
import com.aCompany.wms.repository.StockLocationRepository;
import com.aCompany.wms.repository.StockRepository;
import com.aCompany.wms.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/admin/stock")
public class StockController {

    private final StockService stockService;
    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final StockLocationRepository locationRepository;

    @Autowired
    public StockController(StockService stockService, 
                         StockRepository stockRepository,
                         ProductRepository productRepository,
                         StockLocationRepository locationRepository) {
        this.stockService = stockService;
        this.stockRepository = stockRepository;
        this.productRepository = productRepository;
        this.locationRepository = locationRepository;
    }

    @GetMapping("/stockView")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewStockPage(Model model) {
        List<Stock> allStock = stockRepository.findAll();
        model.addAttribute("items", allStock);
        model.addAttribute("allStock", allStock);
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("locations", locationRepository.findAll());
        return "/admin/stockView";  // This should resolve to src/main/resources/templates/admin/stockView.html
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addStock(@RequestParam Long productId,
                          @RequestParam Long locationId,
                          @RequestParam int quantity,
                          @RequestParam(required = false) String batchNumber) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + productId));
            
        StockLocation location = locationRepository.findById(locationId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid location ID: " + locationId));
            
        Stock stock = new Stock(product, location, quantity, batchNumber, null, null);
        stockRepository.save(stock);
        
        return "redirect:/admin/stock";
    }

    @PostMapping("remove")
    @PreAuthorize("hasRole('ADMIN')")
    public String removeStock(@RequestParam Long productId,
                              @RequestParam Long locationId,
                              @RequestParam int quantity,
                              @RequestParam(required = false) String batchNumber) {
        try {
            stockService.removeStock(productId, locationId, quantity, batchNumber);
            return "redirect:/admin/stock?success=Stock+removed+successfully";
        } catch (ProductNotFoundException e) {
            return "redirect:/admin/stock?error=" + URLEncoder.encode("Product not found: " + e.getMessage(), StandardCharsets.UTF_8);
        } catch (LocationNotFoundException e) {
            return "redirect:/admin/stock?error=" + URLEncoder.encode("Location not found: " + e.getMessage(), StandardCharsets.UTF_8);
        } catch (StockNotFoundException e) {
            return "redirect:/admin/stock?error=" + URLEncoder.encode("Stock not found: " + e.getMessage(), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return "redirect:/admin/stock?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
        }
    }



    @PostMapping("/adjust/{id}")
    public String adjustStock(@PathVariable Long id,
                            @RequestParam int quantity) {
        Stock stock = stockRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid stock ID: " + id));
            
        stock.setQuantity(quantity);
        stockRepository.save(stock);
        
        return "redirect:/admin/stock";
    }

    @GetMapping("/api/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Product>>> getAllStockApi() {
        List<Product> allStock = stockService.getAllStock();
        return ResponseEntity.ok(ApiResponse.success(allStock));
    }


}

