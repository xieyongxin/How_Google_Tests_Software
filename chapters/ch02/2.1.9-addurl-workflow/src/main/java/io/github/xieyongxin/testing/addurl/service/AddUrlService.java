package io.github.xieyongxin.testing.addurl.service;

import io.github.xieyongxin.testing.addurl.protocol.AddUrlProto;

/** AddUrl 后端服务契约。 */
public interface AddUrlService {

    AddUrlProto.AddUrlReply add(AddUrlProto.AddUrlRequest request);
}
