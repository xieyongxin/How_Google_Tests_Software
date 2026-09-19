package io.github.xieyongxin.testing.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.xieyongxin.testing.fakes.FakeInventoryService;
import io.github.xieyongxin.testing.fakes.FakePaymentService;
import io.github.xieyongxin.testing.fakes.PaymentAttempt;
import io.github.xieyongxin.testing.order.OrderRequest;
import io.github.xieyongxin.testing.order.OrderResult;
import io.github.xieyongxin.testing.order.OrderService;
import io.github.xieyongxin.testing.order.OrderStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class OrderWorkflowIT {

    private static final OrderRequest REQUEST =
            new OrderRequest("customer-1", "book-1", 2, new BigDecimal("49.90"));

    @Test
    void completesOrderAcrossInventoryAndPaymentServices() {
        FakeInventoryService inventory = new FakeInventoryService(Map.of("book-1", 5));
        FakePaymentService payment = new FakePaymentService(true);
        OrderService orders = new OrderService(inventory, payment);

        OrderResult result = orders.placeOrder(REQUEST);

        assertEquals(OrderStatus.SUCCESS, result.status());
        assertTrue(result.confirmationId() != null && !result.confirmationId().isBlank());
        assertEquals(3, inventory.stockOf("book-1"));
        assertEquals(
                List.of(new PaymentAttempt("customer-1", new BigDecimal("99.80"))),
                payment.attempts());
    }

    @Test
    void leavesCollaboratorsUnchangedWhenInventoryIsInsufficient() {
        FakeInventoryService inventory = new FakeInventoryService(Map.of("book-1", 1));
        FakePaymentService payment = new FakePaymentService(true);
        OrderService orders = new OrderService(inventory, payment);

        OrderResult result = orders.placeOrder(REQUEST);

        assertEquals(OrderStatus.OUT_OF_STOCK, result.status());
        assertEquals(1, inventory.stockOf("book-1"));
        assertTrue(payment.attempts().isEmpty());
    }

    @Test
    void restoresInventoryWhenPaymentServiceDeclinesCharge() {
        FakeInventoryService inventory = new FakeInventoryService(Map.of("book-1", 5));
        FakePaymentService payment = new FakePaymentService(false);
        OrderService orders = new OrderService(inventory, payment);

        OrderResult result = orders.placeOrder(REQUEST);

        assertEquals(OrderStatus.PAYMENT_DECLINED, result.status());
        assertEquals(5, inventory.stockOf("book-1"));
        assertEquals(1, payment.attempts().size());
    }
}

