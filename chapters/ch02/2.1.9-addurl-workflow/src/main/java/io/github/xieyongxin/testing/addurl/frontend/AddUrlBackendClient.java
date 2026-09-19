package io.github.xieyongxin.testing.addurl.frontend;

import io.github.xieyongxin.testing.addurl.protocol.AddUrlProto;

/** Frontend 依赖的后端边界，可由 RPC 客户端或 fake 实现。 */
@FunctionalInterface
public interface AddUrlBackendClient {

    AddUrlProto.AddUrlReply add(AddUrlProto.AddUrlRequest request) throws Exception;
}
