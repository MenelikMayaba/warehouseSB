package com.aCompany.wms.config;

import com.aCompany.wms.model.Stock;
import com.aCompany.wms.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@EnableScheduling
public class SchedulerConfig {
    @Autowired
    private StockService stockService;

    @Scheduled(cron = "0 0 * * * *") // every hour
    public void checkStockLevels() {
        List<Stock> lowStock = stockService.getLowStockItems();
        lowStock.forEach(item -> System.out.println("Reorder: " + item.getProduct().getSku()));
    }
}

