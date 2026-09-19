package io.github.xieyongxin.testing.addurl.service;

import io.github.xieyongxin.testing.addurl.protocol.AddUrlProto;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("small")
class DefaultAddUrlServiceTest {

    @Test
    void storesValidUrl() throws Exception {
        var store = new InMemoryIndexStore();
        var service = new DefaultAddUrlService(store);

        var reply = service.add(AddUrlProto.AddUrlRequest.newBuilder()
                .setUrl("https://example.com")
                .setComment("demo")
                .build());

        assertFalse(reply.hasErrorCode());
        assertTrue(store.contains("https://example.com"));
    }

    @Test
    void rejectsInvalidUrlWithoutWriting() throws Exception {
        var store = new InMemoryIndexStore();
        var service = new DefaultAddUrlService(store);

        var reply = service.add(AddUrlProto.AddUrlRequest.newBuilder()
                .setUrl("not-a-url")
                .build());

        assertTrue(reply.hasErrorCode());
        assertFalse(store.contains("not-a-url"));
    }
}
