package io.github.xieyongxin.testing.addurl.frontend;

import io.github.xieyongxin.testing.addurl.protocol.AddUrlProto;

/** Frontend 的传输无关响应，HTTP 适配器和小型测试都可以复用。 */
public record FrontendResponse(int statusCode, String body, AddUrlProto.AddUrlReply reply) {

    public boolean isSuccess() {
        return statusCode >= 200 && statusCode < 300;
    }
}
