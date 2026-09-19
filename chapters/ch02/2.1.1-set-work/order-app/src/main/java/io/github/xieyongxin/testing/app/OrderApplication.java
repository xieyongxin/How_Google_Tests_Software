package io.github.xieyongxin.testing.app;

import io.github.xieyongxin.testing.fakes.FakeInventoryService;
import io.github.xieyongxin.testing.fakes.FakePaymentService;
import io.github.xieyongxin.testing.order.OrderRequest;
import io.github.xieyongxin.testing.order.OrderResult;
import io.github.xieyongxin.testing.order.OrderService;
import java.math.BigDecimal;
import java.util.Map;

/** Assembles the independently built services into a runnable demonstration. */
public final class OrderApplication {

    private OrderApplication() {}

    /** Runs one successful order from end to end. */
    public static void main(String[] args) {
        FakeInventoryService inventory = new FakeInventoryService(Map.of("book-1", 5));
        FakePaymentService payment = new FakePaymentService(true);
        OrderService orders = new OrderService(inventory, payment);
        OrderRequest request =
                new OrderRequest("customer-1", "book-1", 2, new BigDecimal("49.90"));

        OrderResult result = orders.placeOrder(request);

        System.out.println("status=" + result.status());
        System.out.println("confirmationId=" + result.confirmationId());
        System.out.println("remainingStock=" + inventory.stockOf("book-1"));
        System.out.println("paymentAttempts=" + payment.attempts().size());
    }
}

