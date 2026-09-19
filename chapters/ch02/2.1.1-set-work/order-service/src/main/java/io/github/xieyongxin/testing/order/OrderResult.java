package io.github.xieyongxin.testing.order;

/** Result of an attempt to place an order. */
public record OrderResult(OrderStatus status, String confirmationId) {

    static OrderResult success(String confirmationId) {
        return new OrderResult(OrderStatus.SUCCESS, confirmationId);
    }

    static OrderResult failed(OrderStatus status) {
        return new OrderResult(status, null);
    }
}

