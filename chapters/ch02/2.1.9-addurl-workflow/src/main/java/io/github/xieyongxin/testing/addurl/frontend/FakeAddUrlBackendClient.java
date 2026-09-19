package io.github.xieyongxin.testing.addurl.frontend;

import io.github.xieyongxin.testing.addurl.protocol.AddUrlProto;
import io.github.xieyongxin.testing.addurl.service.AddUrlReplies;
import java.util.ArrayList;
import java.util.List;

/** 可配置的后端 fake，让小型测试不需要网络或真实 RPC。 */
public final class FakeAddUrlBackendClient implements AddUrlBackendClient {

    private final List<AddUrlProto.AddUrlRequest> requests = new ArrayList<>();
    private AddUrlProto.AddUrlReply reply = AddUrlReplies.success();
    private Exception failure;

    public FakeAddUrlBackendClient withReply(AddUrlProto.AddUrlReply configuredReply) {
        this.reply = configuredReply;
        this.failure = null;
        return this;
    }

    public FakeAddUrlBackendClient withFailure(Exception configuredFailure) {
        this.failure = configuredFailure;
        return this;
    }

    @Override
    public synchronized AddUrlProto.AddUrlReply add(AddUrlProto.AddUrlRequest request) throws Exception {
        requests.add(request);
        if (failure != null) {
            throw failure;
        }
        return reply;
    }

    public synchronized List<AddUrlProto.AddUrlRequest> requests() {
        return List.copyOf(requests);
    }
}
