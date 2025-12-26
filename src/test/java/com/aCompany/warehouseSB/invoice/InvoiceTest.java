package com.aCompany.warehouseSB.invoice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Invoice")
class InvoiceTest {
    
    private Invoice invoice;
    private InvoiceItem item1;
    private InvoiceItem item2;
    
    @BeforeEach
    void setUp() {
        invoice = new Invoice();
        item1 = new InvoiceItem();
        item1.setId(1L);
        item1.setQuantity(2);
        
        item2 = new InvoiceItem();
        item2.setId(2L);
        item2.setQuantity(3);
    }
    
    @Nested
    @DisplayName("when new")
    class WhenNew {
        
        @Test
        @DisplayName("should have empty items list")
        void shouldHaveEmptyItemsList() {
            // When & Then
            assertThat(invoice.getItems()).isNotNull().isEmpty();
        }
    }
    
    @Nested
    @DisplayName("when adding items")
    class WhenAddingItems {
        
        @Test
        @DisplayName("should add item to invoice")
        void shouldAddItemToInvoice() {
            // When
            invoice.addItem(item1);
            
            // Then
            assertThat(invoice.getItems())
                .hasSize(1)
                .containsExactly(item1);
            assertThat(item1.getInvoice()).isEqualTo(invoice);
        }
        
        @Test
        @DisplayName("should add multiple items to invoice")
        void shouldAddMultipleItemsToInvoice() {
            // When
            invoice.addItem(item1);
            invoice.addItem(item2);
            
            // Then
            assertThat(invoice.getItems())
                .hasSize(2)
                .containsExactlyInAnyOrder(item1, item2);
            assertThat(item1.getInvoice()).isEqualTo(invoice);
            assertThat(item2.getInvoice()).isEqualTo(invoice);
        }
    }
    
    @Nested
    @DisplayName("when removing items")
    class WhenRemovingItems {
        
        @BeforeEach
        void setUp() {
            invoice.addItem(item1);
            invoice.addItem(item2);
        }
        
        @Test
        @DisplayName("should remove item from invoice")
        void shouldRemoveItemFromInvoice() {
            // When
            invoice.removeItem(item1);
            
            // Then
            assertThat(invoice.getItems())
                .hasSize(1)
                .containsExactly(item2);
            assertThat(item1.getInvoice()).isNull();
        }
        
        @Test
        @DisplayName("should not fail when removing non-existent item")
        void shouldNotFailWhenRemovingNonExistentItem() {
            // Given
            InvoiceItem nonExistentItem = new InvoiceItem();
            nonExistentItem.setId(999L);
            
            // When & Then
            assertDoesNotThrow(() -> invoice.removeItem(nonExistentItem));
        }
    }
}
