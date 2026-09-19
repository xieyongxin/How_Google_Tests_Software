package io.github.xieyongxin.testing.addurl;

import io.github.xieyongxin.testing.addurl.frontend.AddUrlFrontend;
import io.github.xieyongxin.testing.addurl.frontend.ServiceAddUrlBackendClient;
import io.github.xieyongxin.testing.addurl.service.AddUrlReplies;
import io.github.xieyongxin.testing.addurl.service.DefaultAddUrlService;
import io.github.xieyongxin.testing.addurl.service.InMemoryIndexStore;
import java.time.Duration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("medium")
class AddUrlWorkflowIT {

    @Test
    void frontendServiceAndMemoryIndexCollaborate() throws Exception {
        var store = new InMemoryIndexStore();
        var service = new DefaultAddUrlService(store);
        try (var frontend = new AddUrlFrontend(new ServiceAddUrlBackendClient(service),
                Duration.ofSeconds(1))) {
            var response = frontend.handleQuery("url=https%3A%2F%2Fexample.com&comment=medium");

            assertEquals(200, response.statusCode());
            assertTrue(store.contains("https://example.com"));
            assertEquals("medium", store.commentFor("https://example.com"));
        }
    }

    @Test
    void invalidUrlStopsBeforeIndexWrite() throws Exception {
        var store = new InMemoryIndexStore();
        var service = new DefaultAddUrlService(store);
        try (var frontend = new AddUrlFrontend(new ServiceAddUrlBackendClient(service),
                Duration.ofSeconds(1))) {
            var response = frontend.handleQuery("url=ftp%3A%2F%2Fexample.com");

            assertEquals(AddUrlReplies.CLIENT_ERROR, response.statusCode());
            assertTrue(!store.contains("ftp://example.com"));
        }
    }
}
