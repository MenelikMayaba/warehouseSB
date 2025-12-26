package com.aCompany.warehouseSB.picking;

import com.aCompany.warehouseSB.inventory.Item;
import com.aCompany.warehouseSB.inventory.ItemRepository;
import com.aCompany.warehouseSB.inventory.StockTransaction;
import com.aCompany.warehouseSB.inventory.TransactionRepository;
import com.aCompany.warehouseSB.invoice.Invoice;
import com.aCompany.warehouseSB.invoice.InvoiceItem;
import com.aCompany.warehouseSB.invoice.InvoiceRepository;
import com.aCompany.warehouseSB.user.User;
import com.aCompany.warehouseSB.webControllers.PickingTaskDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.aCompany.warehouseSB.invoice.InvoiceStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PickingService {

    private final InvoiceRepository invoiceRepository;
    private final ItemRepository itemRepository;
    private final TransactionRepository stockTransactionRepository;
    private final PickRepository pickRepository;

    public PickingService(
            InvoiceRepository invoiceRepository,
            ItemRepository itemRepository,
            TransactionRepository stockTransactionRepository,
            PickRepository pickRepository
    ) {
        this.invoiceRepository = invoiceRepository;
        this.itemRepository = itemRepository;
        this.stockTransactionRepository = stockTransactionRepository;
        this.pickRepository = pickRepository;
    }

    public List<PickingTaskDTO> getPickingTasks() {
        // Get all invoices that need to be picked
        return invoiceRepository.findByStatus(InvoiceStatus.PICKING.name())
                .stream()
                .map(invoice -> {
                    PickingTaskDTO dto = new PickingTaskDTO();
                    dto.setId(invoice.getId());
                    dto.setOrderNumber(invoice.getOrderNumber());
                    dto.setItemCount(invoice.getItems().size());
                    dto.setPriority(invoice.getPriority());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public int countAssignedPicks() {
        return (int) pickRepository.countByPickerNotNull(PickStatus.IN_PROGRESS);
    }

    public int countCompletedToday() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return pickRepository.countByCompletedAtAfterAndStatus(
            startOfDay, 
            PickStatus.COMPLETED
        );
    }

    public double getAveragePickTime() {
        return (double) pickRepository.findAveragePickTime()
                .orElse(0.0);
    }

    public double getPickingAccuracy() {
        return (double) pickRepository.findPickingAccuracy()
                .orElse(100.0); // Default to 100% if no data
    }

    @Transactional
    public void pickInvoice(Long invoiceId, User picker) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        for (InvoiceItem invoiceItem : invoice.getItems()) {

            Item item = invoiceItem.getItem();
            int requiredQty = invoiceItem.getQuantity();

            if (item.getQuantity() < requiredQty) {
                throw new IllegalStateException(
                        "Insufficient stock for item: " + item.getSku()
                );
            }

            // decrement stock
            item.setQuantity(item.getQuantity() - requiredQty);
            itemRepository.save(item);

            // stock transaction (audit)
            StockTransaction tx = new StockTransaction();
            tx.setItem(item);
            tx.setQuantityChange(-requiredQty);
            tx.setCreatedAt(LocalDateTime.now());
            stockTransactionRepository.save(tx);

            // pick event (who did it)
            Pick pickEvent = new Pick();
            pickEvent.setItem(item);
            pickEvent.setQuantity(requiredQty);
            pickEvent.setPicker(picker);
            pickEvent.setPickedAt(LocalDateTime.now());
            pickRepository.save(pickEvent);
        }
    }

    public Object getPendingInvoices() {
        return invoiceRepository.findByStatus("PENDING");
    }

    public void startPickingTask(Long taskId) {
        Pick pick = pickRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Pick not found"));

        pick.setStatus(PickStatus.IN_PROGRESS);
        pick.setStartedAt(LocalDateTime.now());
        pickRepository.save(pick);
    }

    public void completePickingTask(Long taskId, User currentUser) {
        Invoice invoice = invoiceRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        invoice.setStatus(InvoiceStatus.PICKED);
        invoiceRepository.save(invoice);
    }
}
