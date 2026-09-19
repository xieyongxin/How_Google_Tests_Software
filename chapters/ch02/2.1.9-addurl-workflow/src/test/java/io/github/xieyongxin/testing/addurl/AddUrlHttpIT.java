package io.github.xieyongxin.testing.addurl;

import io.github.xieyongxin.testing.addurl.app.AddUrlHttpServer;
import io.github.xieyongxin.testing.addurl.frontend.AddUrlFrontend;
import io.github.xieyongxin.testing.addurl.frontend.ServiceAddUrlBackendClient;
import io.github.xieyongxin.testing.addurl.service.DefaultAddUrlService;
import io.github.xieyongxin.testing.addurl.service.InMemoryIndexStore;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("large")
class AddUrlHttpIT {

    @Test
    void completeHttpRequestChainReturnsSuccess() throws Exception {
        var store = new InMemoryIndexStore();
        var service = new DefaultAddUrlService(store);
        try (var frontend = new AddUrlFrontend(new ServiceAddUrlBackendClient(service),
                Duration.ofSeconds(1));
             var server = new AddUrlHttpServer(frontend)) {
            server.start();
            URI uri = URI.create(server.endpoint()
                    + "?url=https%3A%2F%2Fwww.example.com%2Fdocs&comment=http");
            var response = send(uri);

            assertEquals(200, response.statusCode());
            assertEquals("http", store.commentFor("https://www.example.com/docs"));
        }
    }

    @Test
    void completeHttpRequestChainReturnsClientError() throws Exception {
        var store = new InMemoryIndexStore();
        var service = new DefaultAddUrlService(store);
        try (var frontend = new AddUrlFrontend(new ServiceAddUrlBackendClient(service),
                Duration.ofSeconds(1));
             var server = new AddUrlHttpServer(frontend)) {
            server.start();
            var response = send(server.endpoint());

            assertEquals(400, response.statusCode());
        }
    }

    private static HttpResponse<String> send(URI uri) throws Exception {
        var request = HttpRequest.newBuilder(uri).GET().build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }
}
