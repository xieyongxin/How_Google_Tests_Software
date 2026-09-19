package io.github.xieyongxin.testing.addurl.frontend;

import io.github.xieyongxin.testing.addurl.protocol.AddUrlProto;
import io.github.xieyongxin.testing.addurl.service.AddUrlReplies;
import java.time.Duration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("small")
class AddUrlFrontendTest {

    @Test
    void parsesUrlAndDecodedComment() {
        var fake = new FakeAddUrlBackendClient();
        try (var frontend = new AddUrlFrontend(fake, Duration.ofSeconds(1))) {
            var response = frontend.handleQuery("url=https%3A%2F%2Fexample.com%2Fa&comment=hello+world");

            assertTrue(response.isSuccess());
            assertEquals("https://example.com/a", fake.requests().getFirst().getUrl());
            assertEquals("hello world", fake.requests().getFirst().getComment());
        }
    }

    @Test
    void missingUrlIsRejectedWithoutCallingBackend() {
        var fake = new FakeAddUrlBackendClient();
        try (var frontend = new AddUrlFrontend(fake, Duration.ofSeconds(1))) {
            var response = frontend.handleQuery("comment=only-comment");

            assertEquals(AddUrlReplies.CLIENT_ERROR, response.statusCode());
            assertTrue(fake.requests().isEmpty());
        }
    }

    @Test
    void mapsBackendReplyErrorToHttpStatus() {
        var fake = new FakeAddUrlBackendClient().withReply(
                AddUrlReplies.error(AddUrlReplies.BACKEND_ERROR, "存储不可用"));
        try (var frontend = new AddUrlFrontend(fake, Duration.ofSeconds(1))) {
            var response = frontend.handleQuery("url=https%3A%2F%2Fexample.com");

            assertEquals(AddUrlReplies.BACKEND_ERROR, response.statusCode());
            assertEquals("存储不可用", response.body());
        }
    }

    @Test
    void mapsBackendExceptionToServerError() {
        var fake = new FakeAddUrlBackendClient().withFailure(new IllegalStateException("boom"));
        try (var frontend = new AddUrlFrontend(fake, Duration.ofSeconds(1))) {
            var response = frontend.handleQuery("url=https%3A%2F%2Fexample.com");

            assertEquals(AddUrlReplies.BACKEND_ERROR, response.statusCode());
            assertFalse(response.isSuccess());
        }
    }

    @Test
    void mapsSlowBackendToGatewayTimeout() {
        var fake = new FakeAddUrlBackendClient();
        var delayed = new DelayedAddUrlBackendClient(fake, Duration.ofMillis(200));
        try (var frontend = new AddUrlFrontend(delayed, Duration.ofMillis(20))) {
            var response = frontend.handleQuery("url=https%3A%2F%2Fexample.com");

            assertEquals(AddUrlReplies.TIMEOUT, response.statusCode());
            assertEquals(AddUrlReplies.TIMEOUT, response.reply().getErrorCode());
        }
    }

    @Test
    void rejectsMalformedQueryEncoding() {
        var fake = new FakeAddUrlBackendClient();
        try (var frontend = new AddUrlFrontend(fake, Duration.ofSeconds(1))) {
            var response = frontend.handleQuery("url=%ZZ");

            assertEquals(AddUrlReplies.CLIENT_ERROR, response.statusCode());
            assertTrue(fake.requests().isEmpty());
        }
    }
}
