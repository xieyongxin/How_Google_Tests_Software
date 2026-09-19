package io.github.xieyongxin.testing.fakes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("small")
class FakePaymentServiceTest {

    @Test
    void returnsConfiguredDecisionAndRecordsAttempts() {
        FakePaymentService payment = new FakePaymentService(false);

        assertFalse(payment.charge("customer-1", new BigDecimal("20.00")));
        payment.setApproved(true);
        assertTrue(payment.charge("customer-2", new BigDecimal("30.00")));

        assertEquals(
                java.util.List.of(
                        new PaymentAttempt("customer-1", new BigDecimal("20.00")),
                        new PaymentAttempt("customer-2", new BigDecimal("30.00"))),
                payment.attempts());
    }
}
