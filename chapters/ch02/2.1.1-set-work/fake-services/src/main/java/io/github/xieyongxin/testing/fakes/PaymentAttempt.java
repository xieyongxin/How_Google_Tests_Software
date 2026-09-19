package io.github.xieyongxin.testing.fakes;

import java.math.BigDecimal;

/** A payment call recorded by {@link FakePaymentService}. */
public record PaymentAttempt(String customerId, BigDecimal amount) {}

