package com.aCompany.warehouseSB.invoice;

import com.aCompany.warehouseSB.inventory.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("InvoiceItem")
class InvoiceItemTest {
    
    private InvoiceItem invoiceItem;
    
    @Mock
    private Invoice mockInvoice;
    
    @Mock
    private Item mockItem;
    
    @BeforeEach
    void setUp() {
        invoiceItem = new InvoiceItem();
        invoiceItem.setId(1L);
        invoiceItem.setQuantity(5);
    }
    
    @Nested
    @DisplayName("when new")
    class WhenNew {
        
        @Test
        @DisplayName("should have default values")
        void shouldHaveDefaultValues() {
            // Given
            InvoiceItem newItem = new InvoiceItem();
            
            // Then
            assertThat(newItem.getId()).isNull();
            assertThat(newItem.getQuantity()).isZero();
            assertThat(newItem.getInvoice()).isNull();
            assertThat(newItem.getItem()).isNull();
        }
    }
    
    @Nested
    @DisplayName("when setting invoice")
    class WhenSettingInvoice {
        
        @Test
        @DisplayName("should set invoice")
        void shouldSetInvoice() {
            // When
            invoiceItem.setInvoice(mockInvoice);
            
            // Then
            assertThat(invoiceItem.getInvoice()).isEqualTo(mockInvoice);
        }

        @Test
        @DisplayName("should update both sides of relationship")
        void shouldUpdateBothSidesOfRelationship() {
            // Given
            Invoice invoice = new Invoice();
            InvoiceItem item = new InvoiceItem();

            // When
            invoice.addItem(item);

            // Then
            assertThat(invoice.getItems()).contains(item);
            assertThat(item.getInvoice()).isEqualTo(invoice);
        }
    }
    
    @Nested
    @DisplayName("when setting item")
    class WhenSettingItem {
        
        @Test
        @DisplayName("should set item")
        void shouldSetItem() {
            // When
            invoiceItem.setItem(mockItem);
            
            // Then
            assertThat(invoiceItem.getItem()).isEqualTo(mockItem);
        }
    }
    
    @Nested
    @DisplayName("when calculating total price")
    class WhenCalculatingTotalPrice {
        
        @Test
        @DisplayName("should return correct total price")
        void shouldReturnCorrectTotalPrice() {
            // Given
            when(mockItem.getPrice()).thenReturn(10.0);
            invoiceItem.setItem(mockItem);
            invoiceItem.setQuantity(3);
            
            // When
            double totalPrice = invoiceItem.getTotalPrice();
            
            // Then
            assertThat(totalPrice).isEqualTo(30.0);
        }
        
        @Test
        @DisplayName("should return zero when item is not set")
        void shouldReturnZeroWhenItemNotSet() {
            // When
            double totalPrice = invoiceItem.getTotalPrice();
            
            // Then
            assertThat(totalPrice).isZero();
        }
    }
    
    @Nested
    @DisplayName("when checking equality")
    class WhenCheckingEquality {
        
        @Test
        @DisplayName("should be equal when IDs are equal")
        void shouldBeEqualWhenIdsAreEqual() {
            // Given
            InvoiceItem anotherItem = new InvoiceItem();
            anotherItem.setId(1L);
            
            // Then
            assertThat(invoiceItem).isEqualTo(anotherItem);
            assertThat(invoiceItem.hashCode()).isEqualTo(anotherItem.hashCode());
        }
        
        @Test
        @DisplayName("should not be equal when IDs are different")
        void shouldNotBeEqualWhenIdsAreDifferent() {
            // Given
            InvoiceItem anotherItem = new InvoiceItem();
            anotherItem.setId(2L);
            
            // Then
            assertThat(invoiceItem).isNotEqualTo(anotherItem);
        }
        
        @Test
        @DisplayName("should not be equal to null")
        void shouldNotBeEqualToNull() {
            assertThat(invoiceItem.equals(null)).isFalse();
        }
        
        @Test
        @DisplayName("should not be equal to different class")
        void shouldNotBeEqualToDifferentClass() {
            assertThat(invoiceItem.equals(new Object())).isFalse();
        }
    }
}
