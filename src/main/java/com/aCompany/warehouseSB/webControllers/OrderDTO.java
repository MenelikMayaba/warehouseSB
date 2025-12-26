package com.aCompany.warehouseSB.webControllers;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderDTO {
    private Long id;
    private String orderNumber;
    private String customerName;
    private int itemCount;
    private String priority;
    private String priorityClass;
    private String status;
    private String statusClass;
    private LocalDateTime assignedAt;
}
