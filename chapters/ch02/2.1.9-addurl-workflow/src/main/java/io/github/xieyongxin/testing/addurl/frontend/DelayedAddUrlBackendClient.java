package io.github.xieyongxin.testing.addurl.frontend;

import io.github.xieyongxin.testing.addurl.protocol.AddUrlProto;
import java.time.Duration;

/** 可控延迟的后端 fake，用于验证超时和取消路径。 */
public final class DelayedAddUrlBackendClient implements AddUrlBackendClient {

    private final AddUrlBackendClient delegate;
    private final Duration delay;

    public DelayedAddUrlBackendClient(AddUrlBackendClient delegate, Duration delay) {
        this.delegate = delegate;
        this.delay = delay;
    }

    @Override
    public AddUrlProto.AddUrlReply add(AddUrlProto.AddUrlRequest request) throws Exception {
        try {
            Thread.sleep(delay.toMillis());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw exception;
        }
        return delegate.add(request);
    }
}
