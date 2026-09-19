package io.github.xieyongxin.testing.addurl.frontend;

import io.github.xieyongxin.testing.addurl.protocol.AddUrlProto;
import io.github.xieyongxin.testing.addurl.service.AddUrlService;

/** 把本地服务适配成 Frontend 所需的后端客户端。 */
public final class ServiceAddUrlBackendClient implements AddUrlBackendClient {

    private final AddUrlService service;

    public ServiceAddUrlBackendClient(AddUrlService service) {
        this.service = service;
    }

    @Override
    public AddUrlProto.AddUrlReply add(AddUrlProto.AddUrlRequest request) {
        return service.add(request);
    }
}
