package io.github.xieyongxin.testing.order;

import java.math.BigDecimal;

/** Input required to place an order. */
public record OrderRequest(
        String customerId, String productId, int quantity, BigDecimal unitPrice) {

    /** Returns the amount that should be charged for this request. */
    public BigDecimal totalAmount() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}

