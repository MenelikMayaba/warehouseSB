package com.aCompany.wms.controller;

import com.aCompany.wms.model.Product;
import com.aCompany.wms.repository.ProductRepository;
import org.springframework.ui.Model;
import com.aCompany.wms.repository.StockRepository;
import com.aCompany.wms.service.PackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/packing")
public class PackingController {

    @Autowired
    private PackingService packingService;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public String viewPackingPage(Model model) {
        model.addAttribute("items", stockRepository.findAll());
        return "packing";
    }

    @PostMapping("/check/{id}/{expectedQty}")
    public String qualityCheck(@PathVariable Long id, @PathVariable int expectedQty) {
        Product item = stockRepository.findById(id).orElseThrow().getProduct();
        boolean ok = packingService.qualityCheck(item, expectedQty);
        if (!ok) {
            packingService.recoverFailedPack(item);
        }
        return "redirect:/packing";
    }


    // Checks if quantity is sufficient
    public boolean qualityCheck(Product product, int expectedQty) {
        if(product.getQuantity() >= expectedQty) {
            return true;
        } else {
            System.out.println("Quality check failed for SKU: " + product.getSku());
            return false;
        }
    }

    // Recovery: increment quantity or log issue
    public void recoverFailedPack(Product product) {
        product.setQuantity(product.getQuantity() + 1);
        productRepository.save(product);
        System.out.println("Recovered failed pack for SKU: " + product.getSku());
    }
}

