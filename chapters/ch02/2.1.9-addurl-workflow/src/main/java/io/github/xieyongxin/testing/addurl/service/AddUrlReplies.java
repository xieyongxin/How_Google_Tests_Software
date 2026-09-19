package io.github.xieyongxin.testing.addurl.service;

import io.github.xieyongxin.testing.addurl.protocol.AddUrlProto;

/** 集中维护协议错误码，避免前端和后端各自定义一套语义。 */
public final class AddUrlReplies {

    public static final int CLIENT_ERROR = 400;
    public static final int BACKEND_ERROR = 500;
    public static final int TIMEOUT = 504;

    private AddUrlReplies() {
    }

    public static AddUrlProto.AddUrlReply success() {
        return AddUrlProto.AddUrlReply.getDefaultInstance();
    }

    public static AddUrlProto.AddUrlReply error(int code, String details) {
        return AddUrlProto.AddUrlReply.newBuilder()
                .setErrorCode(code)
                .setErrorDetails(details)
                .build();
    }
}
