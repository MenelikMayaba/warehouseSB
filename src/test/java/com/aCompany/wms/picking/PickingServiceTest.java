package com.aCompany.wms.picking;

import com.aCompany.wms.exceptions.InvoiceNotFoundException;
import com.aCompany.wms.model.Invoice;
import com.aCompany.wms.model.StockTransaction;
import com.aCompany.wms.repository.InvoiceRepository;
import com.aCompany.wms.repository.StockTransactionRepository;
import com.aCompany.wms.service.PickingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
@DisplayName("Picking Service")
class PickingServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private StockTransactionRepository transactionRepository;

    @InjectMocks
    private PickingService pickingService;

    private static final String TEST_USERNAME = "testUser";
    private static final Long INVOICE_ID = 1L;
    private Invoice testInvoice;

    @BeforeEach
    void setUp() {
        // Initialize a test invoice before each test
        testInvoice = new Invoice();
        testInvoice.setId(INVOICE_ID);
        testInvoice.setPicked(false);
    }

    @Nested
    @DisplayName("POST /picking/pick/{id}")
    class PickInvoice {

        @Test
        @DisplayName("when invoice exists, it is picked and user is redirected")
        void picksInvoiceSuccessfully() {
            // Given: an invoice exists in the system and has not yet been picked,
            // and the persistence layer is available to record both the invoice update
            // and the resulting stock transaction
            when(invoiceRepository.findById(INVOICE_ID)).thenReturn(Optional.of(testInvoice));
            when(transactionRepository.save(any(StockTransaction.class)))
                    .thenReturn(new StockTransaction());

            // When: the picking service is asked to pick the invoice by its id
            pickingService.pickInvoiceById(INVOICE_ID);

            // Then: the invoice is marked as picked,
            // the updated invoice is persisted,
            // and a stock transaction is recorded to reflect the pick operation
            assertThat(testInvoice.isPicked()).isTrue();
            verify(invoiceRepository).save(testInvoice);
            verify(transactionRepository).save(any(StockTransaction.class));
        }

        @Test
        @DisplayName("when invoice does not exist, throw InvoiceNotFoundException")
        void returns404WhenInvoiceMissing() {
            // Given: no invoice exists in the repository for the requested id
            Long missingId = 999L;
            when(invoiceRepository.findById(missingId)).thenReturn(Optional.empty());

            // When / Then: attempting to pick a non-existent invoice
            // results in a domain-specific exception and no side effects
            assertThrows(InvoiceNotFoundException.class, () ->
                    pickingService.pickInvoiceById(missingId));

            verify(invoiceRepository).findById(missingId);
            verifyNoMoreInteractions(invoiceRepository, transactionRepository);
        }

        @Test
        @DisplayName("when repository throws exception, it propagates")
        void propagatesRepositoryException() {
            // Given: a lower-level data access failure occurs while retrieving the invoice
            Long invoiceId = 1L;
            when(invoiceRepository.findById(invoiceId))
                    .thenThrow(new RuntimeException("DB down"));

            // When / Then: the service does not attempt to recover or mask the failure,
            // and the exception is allowed to propagate to the caller
            assertThrows(RuntimeException.class, () ->
                    pickingService.pickInvoiceById(invoiceId));

            verify(invoiceRepository).findById(invoiceId);
            verifyNoMoreInteractions(invoiceRepository, transactionRepository);
        }
    }

}