package com.aCompany.wms.picking;

import com.aCompany.wms.controller.PickingController;
import com.aCompany.wms.exceptions.InvoiceNotFoundException;
import com.aCompany.wms.model.Invoice;
import com.aCompany.wms.service.PickingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Picking Controller")
class PickingControllerTest {

    @Mock
    private PickingService pickingService;

    @InjectMocks
    private PickingController pickingController;

    private MockMvc mockMvc;
    private Invoice testInvoice1;
    private Invoice testInvoice2;

    @BeforeEach
    void setUp() {
        // Initialize MockMvc with the controller
        mockMvc = MockMvcBuilders.standaloneSetup(pickingController).build();

        // Setup test data
        testInvoice1 = new Invoice();
        testInvoice1.setId(1L);
        testInvoice1.setPicked(false);
        testInvoice1.setStatus("PRIORITY");

        testInvoice2 = new Invoice();
        testInvoice2.setId(2L);
        testInvoice2.setPicked(false);
        testInvoice2.setStatus("NORMAL");
    }

    @Nested
    @DisplayName("POST /picking/pick/{id}")
    class PickInvoice {

        @Test
        void redirectsWhenPickSucceeds() throws Exception {
            // Given: the service can successfully pick the invoice
            willDoNothing().given(pickingService).pickInvoiceById(1L);

            // When / Then: request succeeds and user is redirected
            mockMvc.perform(post("/picking/pick/{id}", 1L))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/picking"));

            then(pickingService).should().pickInvoiceById(1L);
        }

//        @Test
//        void returns404WhenInvoiceMissing() throws Exception {
//            // Given: the invoice does not exist
//            willThrow(new InvoiceNotFoundException(1L))
//                    .given(pickingService).pickInvoiceById(1L);
//
//            // When / Then: client receives 404
//            mockMvc.perform(post("/picking/pick/{id}", 1L))
//                    .andExpect(status().isNotFound());
//        }

        @Test
        void returns500OnUnexpectedFailure() throws Exception {
            // Given: an unexpected service failure
            doThrow(new RuntimeException("DB down"))
                    .when(pickingService).pickInvoiceById(1L);

            // When / Then: server error is returned
            mockMvc.perform(post("/picking/pick/{id}", 1L))
                    .andExpect(status().isInternalServerError())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof RuntimeException))
                    .andExpect(result -> assertEquals("DB down", result.getResolvedException().getMessage()));
        }
    }

}