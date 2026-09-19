package io.github.xieyongxin.testing.addurl.service;

import io.github.xieyongxin.testing.addurl.protocol.AddUrlProto;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/** 校验请求并写入索引的正式服务实现。 */
public final class DefaultAddUrlService implements AddUrlService {

    private final IndexStore indexStore;

    public DefaultAddUrlService(IndexStore indexStore) {
        this.indexStore = indexStore;
    }

    @Override
    public AddUrlProto.AddUrlReply add(AddUrlProto.AddUrlRequest request) {
        if (request == null || !request.hasUrl() || !isValidUrl(request.getUrl())) {
            return AddUrlReplies.error(AddUrlReplies.CLIENT_ERROR, "url 必须是合法的 http/https 地址");
        }

        String comment = request.hasComment() ? request.getComment() : "";
        try {
            indexStore.add(request.getUrl(), comment);
            return AddUrlReplies.success();
        } catch (IOException | RuntimeException exception) {
            return AddUrlReplies.error(AddUrlReplies.BACKEND_ERROR, "索引写入失败");
        }
    }

    private static boolean isValidUrl(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            URI uri = new URI(value);
            return ("http".equalsIgnoreCase(uri.getScheme())
                    || "https".equalsIgnoreCase(uri.getScheme()))
                    && uri.getHost() != null;
        } catch (URISyntaxException exception) {
            return false;
        }
    }
}
