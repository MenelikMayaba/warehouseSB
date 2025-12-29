//package com.aCompany.warehouseSB.picking;
//
//import com.aCompany.wms.User.Role;
//import com.aCompany.wms.User.User;
//import com.aCompany.wms.controller.PickingController;
//import com.aCompany.wms.repository.UserRepository;
//import com.aCompany.wms.service.PickingService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("PickingController")
//class PickingControllerTest {
//
//    @Mock
//    private PickingService pickingService;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private PickingController pickingController;
//
//    private User testUser;
//    private static final Long TEST_INVOICE_ID = 1L;
//    private static final Long TEST_PICKER_ID = 100L;
//
//    @BeforeEach
//    void setUp() {
//
//
//        testUser = new User();
//        testUser.setId(TEST_PICKER_ID); // Set ID directly
//        testUser.setUsername("test.picker");
//        testUser.setPassword("password");
//        testUser.setRole(Role.ROLE_PICKER);
//        // Don't save to mock repository here
//    }
//
//    @Nested
//    @DisplayName("when picking an invoice")
//    class WhenPickingInvoice {
//
//        @Test
//        @DisplayName("should successfully pick invoice with valid picker and invoice")
//        void shouldSuccessfullyPickInvoice() {
//            // Given
//            Authentication auth = mock(Authentication.class);
//            when(auth.getPrincipal()).thenReturn(testUser);
//            SecurityContextHolder.getContext().setAuthentication(auth);
//
//            doNothing().when(pickingService).pickInvoice(anyLong(), any(User.class));
//
//            // When & Then
//            assertDoesNotThrow(() ->
//                    pickingController.pickInvoice(TEST_INVOICE_ID, null) // testPickerId is not used
//            );
//
//            verifyNoInteractions(userRepository); // Shouldn't use userRepository
//            verify(pickingService, times(1)).pickInvoice(TEST_INVOICE_ID, testUser);
//        }
//
//        @Test
//        @DisplayName("should propagate exceptions from picking service")
//        void shouldPropagateExceptionsFromService() {
//            // Given
//            Authentication auth = mock(Authentication.class);
//            when(auth.getPrincipal()).thenReturn(testUser);
//            SecurityContextHolder.getContext().setAuthentication(auth);
//
//            doThrow(new IllegalStateException("Insufficient stock"))
//                    .when(pickingService).pickInvoice(anyLong(), any(User.class));
//
//            // When & Then
//            IllegalStateException exception = assertThrows(IllegalStateException.class,
//                    () -> pickingController.pickInvoice(TEST_INVOICE_ID, null)
//            );
//            assertThat(exception.getMessage()).contains("Insufficient stock");
//        }
//    }
//
//    @Nested
//    @DisplayName("with invalid input")
//    class WithInvalidInput {
//
//        @Test
//        @DisplayName("should throw exception for null invoice ID")
//        void shouldThrowExceptionForNullInvoiceId() {
//            // When & Then
//            assertThrows(IllegalArgumentException.class,
//                    () -> pickingController.pickInvoice(null, null)
//            );
//            verifyNoInteractions(pickingService);
//        }
//    }
//}