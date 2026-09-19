package io.github.xieyongxin.testing.contracts;

import java.math.BigDecimal;

/** Contract used by order processing to charge a customer. */
public interface PaymentService {

    /**
     * Attempts to charge a customer.
     *
     * @param customerId customer identifier
     * @param amount amount to charge
     * @return {@code true} when payment is approved
     */
    boolean charge(String customerId, BigDecimal amount);
}

