package com.aCompany.warehouseSB.picking;

import com.aCompany.warehouseSB.inventory.Item;
import com.aCompany.warehouseSB.inventory.ItemRepository;
import com.aCompany.warehouseSB.inventory.StockTransaction;
import com.aCompany.warehouseSB.inventory.TransactionRepository;
import com.aCompany.warehouseSB.invoice.Invoice;
import com.aCompany.warehouseSB.invoice.InvoiceItem;
import com.aCompany.warehouseSB.invoice.InvoiceRepository;
import com.aCompany.warehouseSB.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PickingService")
class PickingServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private PickRepository pickRepository;

    @InjectMocks
    private PickingService pickingService;

    @Captor
    private ArgumentCaptor<StockTransaction> transactionCaptor;

    @Captor
    private ArgumentCaptor<Pick> pickEventCaptor;

    private Invoice testInvoice;
    private Item testItem1;
    private Item testItem2;
    private User testPicker;
    private static final Long TEST_INVOICE_ID = 1L;

    @BeforeEach
    void setUp() {
        // Setup test data
        testPicker = new User();
        testPicker.setId(100L);
        testPicker.setUsername("test.picker");

        testItem1 = new Item();
        testItem1.setId(1L);
        testItem1.setSku("ITEM001");
        testItem1.setName("Test Item 1");
        testItem1.setPrice(10.0);
        testItem1.setQuantity(10);

        testItem2 = new Item();
        testItem2.setId(2L);
        testItem2.setSku("ITEM002");
        testItem2.setName("Test Item 2");
        testItem2.setPrice(20.0);
        testItem2.setQuantity(5);

        // Setup invoice with items
        testInvoice = new Invoice();
        testInvoice.setId(TEST_INVOICE_ID);
        
        InvoiceItem invoiceItem1 = new InvoiceItem();
        invoiceItem1.setId(1L);
        invoiceItem1.setItem(testItem1);
        invoiceItem1.setQuantity(2);
        
        InvoiceItem invoiceItem2 = new InvoiceItem();
        invoiceItem2.setId(2L);
        invoiceItem2.setItem(testItem2);
        invoiceItem2.setQuantity(1);
        
        testInvoice.getItems().add(invoiceItem1);
        testInvoice.getItems().add(invoiceItem2);
    }

    @Nested
    @DisplayName("when picking an invoice")
    class WhenPickingInvoice {

        @Test
        @DisplayName("should process all items in the invoice")
        void shouldProcessAllItemsInInvoice() {
            // Given
            when(invoiceRepository.findById(TEST_INVOICE_ID)).thenReturn(Optional.of(testInvoice));
            when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(transactionRepository.save(any(StockTransaction.class))).thenReturn(new StockTransaction());
            when(pickRepository.save(any(Pick.class))).thenReturn(new Pick());

            // When
            pickingService.pickInvoice(TEST_INVOICE_ID, testPicker);

            // Then
            verify(invoiceRepository).findById(TEST_INVOICE_ID);
            
            // Verify items were updated
            assertThat(testItem1.getQuantity()).isEqualTo(8); // 10 - 2
            assertThat(testItem2.getQuantity()).isEqualTo(4);  // 5 - 1
            
            // Verify stock transactions were created
            verify(transactionRepository, times(2)).save(transactionCaptor.capture());
            assertThat(transactionCaptor.getAllValues())
                .extracting(StockTransaction::getQuantityChange)
                .containsExactlyInAnyOrder(-2, -1);
                
            // Verify pick events were created
            verify(pickRepository, times(2)).save(pickEventCaptor.capture());
            assertThat(pickEventCaptor.getAllValues())
                .extracting(Pick::getQuantity)
                .containsExactlyInAnyOrder(2, 1);
        }

        @Test
        @DisplayName("should throw exception when invoice not found")
        void shouldThrowExceptionWhenInvoiceNotFound() {
            // Given
            when(invoiceRepository.findById(TEST_INVOICE_ID)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> pickingService.pickInvoice(TEST_INVOICE_ID, testPicker))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invoice not found");
                
            verifyNoInteractions(itemRepository, transactionRepository, pickRepository);
        }

        @Test
        @DisplayName("should throw exception when insufficient stock")
        void shouldThrowExceptionWhenInsufficientStock() {
            // Given
            testItem1.setQuantity(1); // Not enough for required quantity of 2
            when(invoiceRepository.findById(TEST_INVOICE_ID)).thenReturn(Optional.of(testInvoice));

            // When & Then
            assertThatThrownBy(() -> pickingService.pickInvoice(TEST_INVOICE_ID, testPicker))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient stock");
                
            // Verify no items were updated
            assertThat(testItem1.getQuantity()).isEqualTo(1);
            assertThat(testItem2.getQuantity()).isEqualTo(5);
            
            // Verify no transactions or pick events were created
            verifyNoInteractions(transactionRepository, pickRepository);
        }
    }

    @Nested
    @DisplayName("with empty invoice")
    class WithEmptyInvoice {
        
        @BeforeEach
        void setUp() {
            testInvoice.getItems().clear();
        }
        
        @Test
        @DisplayName("should handle empty invoice")
        void shouldHandleEmptyInvoice() {
            // Given
            when(invoiceRepository.findById(TEST_INVOICE_ID)).thenReturn(Optional.of(testInvoice));
            
            // When
            assertDoesNotThrow(() -> pickingService.pickInvoice(TEST_INVOICE_ID, testPicker));
            
            // Then
            verify(invoiceRepository).findById(TEST_INVOICE_ID);
            verifyNoInteractions(itemRepository, transactionRepository, pickRepository);
        }
    }
}
