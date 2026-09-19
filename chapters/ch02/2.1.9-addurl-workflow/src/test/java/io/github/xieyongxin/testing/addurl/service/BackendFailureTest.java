package io.github.xieyongxin.testing.addurl.service;

import io.github.xieyongxin.testing.addurl.protocol.AddUrlProto;
import java.io.IOException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("small")
class BackendFailureTest {

    @Test
    void mapsStorageFailureToStableErrorWithoutLeakingException() {
        IndexStore failingStore = new IndexStore() {
            @Override
            public void add(String url, String comment) throws IOException {
                throw new IOException("disk path should not escape the API");
            }

            @Override
            public boolean contains(String url) {
                return false;
            }
        };
        var service = new DefaultAddUrlService(failingStore);

        var reply = service.add(AddUrlProto.AddUrlRequest.newBuilder()
                .setUrl("https://example.com")
                .build());

        assertTrue(reply.hasErrorCode());
        assertEquals(AddUrlReplies.BACKEND_ERROR, reply.getErrorCode());
        assertEquals("索引写入失败", reply.getErrorDetails());
    }
}
