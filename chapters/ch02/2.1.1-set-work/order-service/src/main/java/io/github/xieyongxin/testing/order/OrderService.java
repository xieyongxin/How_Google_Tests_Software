package io.github.xieyongxin.testing.order;

import io.github.xieyongxin.testing.contracts.InventoryService;
import io.github.xieyongxin.testing.contracts.PaymentService;
import java.math.BigDecimal;
import java.util.UUID;

/** Coordinates inventory and payment to place an order. */
public final class OrderService {

    private final InventoryService inventoryService;
    private final PaymentService paymentService;

    public OrderService(InventoryService inventoryService, PaymentService paymentService) {
        this.inventoryService = inventoryService;
        this.paymentService = paymentService;
    }

    /** Places an order while keeping inventory consistent when payment fails. */
    public OrderResult placeOrder(OrderRequest request) {
        if (!isValid(request)) {
            return OrderResult.failed(OrderStatus.INVALID_REQUEST);
        }

        if (!inventoryService.reserve(request.productId(), request.quantity())) {
            return OrderResult.failed(OrderStatus.OUT_OF_STOCK);
        }

        if (!paymentService.charge(request.customerId(), request.totalAmount())) {
            inventoryService.release(request.productId(), request.quantity());
            return OrderResult.failed(OrderStatus.PAYMENT_DECLINED);
        }

        return OrderResult.success(UUID.randomUUID().toString());
    }

    private boolean isValid(OrderRequest request) {
        return request != null
                && request.customerId() != null
                && !request.customerId().isBlank()
                && request.productId() != null
                && !request.productId().isBlank()
                && request.quantity() > 0
                && request.unitPrice() != null
                && request.unitPrice().compareTo(BigDecimal.ZERO) > 0;
    }
}

