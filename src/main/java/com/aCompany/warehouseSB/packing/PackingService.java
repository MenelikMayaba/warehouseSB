package com.aCompany.warehouseSB.packing;

import com.aCompany.warehouseSB.inventory.Item;
import com.aCompany.warehouseSB.inventory.Stock;
import com.aCompany.warehouseSB.inventory.StockRepository;
import com.aCompany.warehouseSB.invoice.Invoice;
import com.aCompany.warehouseSB.invoice.InvoiceItem;
import com.aCompany.warehouseSB.invoice.InvoiceRepository;
import com.aCompany.warehouseSB.invoice.InvoiceStatus;
import com.aCompany.warehouseSB.scanner.DispatchSticker;
import com.aCompany.warehouseSB.packing.OrderStatus;
import com.aCompany.warehouseSB.scanner.DispatchStickerRepository;
import com.aCompany.warehouseSB.user.User;
import com.aCompany.warehouseSB.webControllers.OrderDTO;
import com.aCompany.warehouseSB.order.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PackingService {

    private final StockRepository stockRepository;
    private final InvoiceRepository invoiceRepository;

    private final OrderRepository orderRepository;

    public Map<String, Object> getPackingStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("ordersToPack", countOrdersToPack());
        stats.put("packedToday", countPackedToday());
        stats.put("avgPackTime", getAveragePackTime());
        stats.put("accuracy", getPackingAccuracy());
        return stats;
    }

    public List<Object> getOrdersForPacking() {
        // Implement logic to get orders for packing
        return Collections.singletonList(orderRepository.findByStatusIn(
                Arrays.asList(OrderStatus.READY_FOR_PACKING, OrderStatus.PACKING_IN_PROGRESS)
        ).stream().map(this::convertToDTO).collect(Collectors.toList()).reversed());
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(order.getStatus() != null ? order.getStatus().name() : "UNKNOWN");
        dto.setAssignedAt(order.getAssignedAt());
        // Set other fields as needed
        dto.setItemCount(0); // Update this when you have order items
        dto.setPriority("NORMAL");
        dto.setPriorityClass("bg-yellow-100 text-yellow-800");
        dto.setStatusClass("bg-blue-100 text-blue-800");

        return dto;
    }


    private final DispatchStickerRepository stickerRepository;

    public PackingService(StockRepository stockRepository,
                          InvoiceRepository invoiceRepository, OrderRepository orderRepository,
                          DispatchStickerRepository stickerRepository) {
        this.stockRepository = stockRepository;
        this.invoiceRepository = invoiceRepository;
        this.orderRepository = orderRepository;
        this.stickerRepository = stickerRepository;
    }

    public void pickInvoice(Long invoiceId, User picker) {

        // 1. Find the invoice
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        // 2. Loop through all items in the invoice
        for (InvoiceItem invoiceItem : invoice.getItems()) {
            Item item = invoiceItem.getItem();
            int quantityNeeded = invoiceItem.getQuantity();

            // 3. Check if stock is enough
            Optional<Stock> stockOpt = stockRepository.findByItem(item);
            if (stockOpt.isEmpty() || stockOpt.get().getQuantity() < quantityNeeded) {
                throw new IllegalArgumentException("Not enough stock for item: " + item.getName());
            }

            // 4. Reduce stock
            Stock stock = stockOpt.get();
            stock.setQuantity(stock.getQuantity() - quantityNeeded);
            stockRepository.save(stock);
        }

        // 5. Mark the invoice as picked
        invoice.setPickedBy(picker);
        invoiceRepository.save(invoice);
    }

    public DispatchInvoice packInvoice(Long invoiceId, User packer) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        invoice.setStatus(InvoiceStatus.PACKED);
        invoice.setPackedBy(packer);
        invoice.setPackedAt(LocalDateTime.now());
        invoiceRepository.save(invoice);

        DispatchInvoice dispatch = new DispatchInvoice(invoice);

        for (InvoiceItem item : invoice.getItems()) {

            DispatchSticker sticker = new DispatchSticker();
            sticker.setBarcode(UUID.randomUUID().toString());
            sticker.setDispatchInvoice(dispatch);
            sticker.setItem(item.getItem());
            sticker.setQuantity(item.getQuantity());
            sticker.setCreatedAt(LocalDateTime.now());

            stickerRepository.save(sticker);
        }

        return dispatch;
    }


    public Object getReadyInvoices() {
        return invoiceRepository.findByStatus("PACKED");
    }

    public void cancelPackingOrder(Long orderId) {
        Invoice invoice = invoiceRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        invoice.setStatus(InvoiceStatus.CANCELLED);
        invoiceRepository.save(invoice);
    }

    public void completePackingOrder(Long orderId) {
        Invoice invoice = invoiceRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        invoice.setStatus(InvoiceStatus.COMPLETED);
        invoiceRepository.save(invoice);
    }

    public Object countOrdersToPack() {
        return invoiceRepository.countByStatus("PENDING");
    }

    public Object countPackedToday() {
        return invoiceRepository.countByStatus("PACKED");
    }

    public Object getAveragePackTime() {
        return invoiceRepository.countByStatus("PACKED");
    }


    public Object getPackingAccuracy() {
        return invoiceRepository.countByStatus("PACKED");
    }


    public void startPackingOrder(Long orderId) {
        Invoice invoice = invoiceRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        invoice.setStatus(InvoiceStatus.PACKING);
        invoiceRepository.save(invoice);
    }
}
