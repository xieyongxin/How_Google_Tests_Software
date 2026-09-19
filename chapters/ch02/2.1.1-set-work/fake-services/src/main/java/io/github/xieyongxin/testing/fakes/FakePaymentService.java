package io.github.xieyongxin.testing.fakes;

import io.github.xieyongxin.testing.contracts.PaymentService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Configurable payment fake that records every attempted charge. */
public final class FakePaymentService implements PaymentService {

    private final List<PaymentAttempt> attempts = new ArrayList<>();
    private boolean approved;

    public FakePaymentService(boolean approved) {
        this.approved = approved;
    }

    /** Changes the response returned by future charges. */
    public synchronized void setApproved(boolean approved) {
        this.approved = approved;
    }

    @Override
    public synchronized boolean charge(String customerId, BigDecimal amount) {
        attempts.add(new PaymentAttempt(customerId, amount));
        return approved;
    }

    /** Returns an immutable snapshot of recorded calls. */
    public synchronized List<PaymentAttempt> attempts() {
        return List.copyOf(attempts);
    }
}

