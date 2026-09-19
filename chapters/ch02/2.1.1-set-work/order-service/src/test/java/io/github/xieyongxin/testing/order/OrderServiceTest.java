package io.github.xieyongxin.testing.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import io.github.xieyongxin.testing.contracts.InventoryService;
import io.github.xieyongxin.testing.contracts.PaymentService;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@Tag("small")
class OrderServiceTest {

    private static final OrderRequest REQUEST =
            new OrderRequest("customer-1", "book-1", 2, new BigDecimal("49.90"));

    @Mock private InventoryService inventoryService;
    @Mock private PaymentService paymentService;

    private OrderService orderService;
    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        orderService = new OrderService(inventoryService, paymentService);
    }

    @org.junit.jupiter.api.AfterEach
    void closeMocks() throws Exception {
        mocks.close();
    }

    @Test
    void placesOrderWhenInventoryAndPaymentSucceed() {
        when(inventoryService.reserve("book-1", 2)).thenReturn(true);
        when(paymentService.charge("customer-1", new BigDecimal("99.80"))).thenReturn(true);

        OrderResult result = orderService.placeOrder(REQUEST);

        assertEquals(OrderStatus.SUCCESS, result.status());
        assertNotNull(result.confirmationId());
        verify(inventoryService).reserve("book-1", 2);
        verify(paymentService).charge("customer-1", new BigDecimal("99.80"));
        verify(inventoryService, never()).release("book-1", 2);
    }

    @Test
    void stopsBeforePaymentWhenInventoryIsInsufficient() {
        when(inventoryService.reserve("book-1", 2)).thenReturn(false);

        OrderResult result = orderService.placeOrder(REQUEST);

        assertEquals(OrderStatus.OUT_OF_STOCK, result.status());
        verify(paymentService, never()).charge("customer-1", new BigDecimal("99.80"));
    }

    @Test
    void releasesInventoryWhenPaymentIsDeclined() {
        when(inventoryService.reserve("book-1", 2)).thenReturn(true);
        when(paymentService.charge("customer-1", new BigDecimal("99.80"))).thenReturn(false);

        OrderResult result = orderService.placeOrder(REQUEST);

        assertEquals(OrderStatus.PAYMENT_DECLINED, result.status());
        verify(inventoryService).release("book-1", 2);
    }

    @Test
    void rejectsNonpositiveQuantityWithoutCallingDependencies() {
        OrderRequest invalid =
                new OrderRequest("customer-1", "book-1", 0, new BigDecimal("49.90"));

        OrderResult result = orderService.placeOrder(invalid);

        assertEquals(OrderStatus.INVALID_REQUEST, result.status());
        verifyNoInteractions(inventoryService, paymentService);
    }

    @Test
    void rejectsNonpositivePriceWithoutCallingDependencies() {
        OrderRequest invalid =
                new OrderRequest("customer-1", "book-1", 2, BigDecimal.ZERO);

        OrderResult result = orderService.placeOrder(invalid);

        assertEquals(OrderStatus.INVALID_REQUEST, result.status());
        verifyNoInteractions(inventoryService, paymentService);
    }

    @Test
    void rejectsNullRequestWithoutCallingDependencies() {
        OrderResult result = orderService.placeOrder(null);

        assertEquals(OrderStatus.INVALID_REQUEST, result.status());
        verifyNoInteractions(inventoryService, paymentService);
    }
}
