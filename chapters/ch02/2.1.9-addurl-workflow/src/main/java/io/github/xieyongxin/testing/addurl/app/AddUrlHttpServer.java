package io.github.xieyongxin.testing.addurl.app;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import io.github.xieyongxin.testing.addurl.frontend.AddUrlFrontend;
import io.github.xieyongxin.testing.addurl.frontend.FrontendResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/** 使用 JDK 内置 HTTP Server 模拟 AddUrlFrontend 的网络边界。 */
public final class AddUrlHttpServer implements AutoCloseable {

    private final HttpServer server;
    private final AddUrlFrontend frontend;

    public AddUrlHttpServer(AddUrlFrontend frontend) throws IOException {
        this(frontend, 0);
    }

    public AddUrlHttpServer(AddUrlFrontend frontend, int port) throws IOException {
        this.frontend = frontend;
        this.server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
        this.server.createContext("/addurl", this::handle);
    }

    public void start() {
        server.start();
    }

    public int port() {
        return server.getAddress().getPort();
    }

    public URI endpoint() {
        return URI.create("http://localhost:" + port() + "/addurl");
    }

    private void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            write(exchange, 405, "只支持 GET");
            return;
        }
        FrontendResponse response = frontend.handleQuery(exchange.getRequestURI().getRawQuery());
        write(exchange, response.statusCode(), response.body());
    }

    private static void write(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    @Override
    public void close() {
        server.stop(0);
        frontend.close();
    }
}
