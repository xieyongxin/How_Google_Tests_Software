package io.github.xieyongxin.testing.addurl.app;

import io.github.xieyongxin.testing.addurl.frontend.AddUrlFrontend;
import io.github.xieyongxin.testing.addurl.frontend.ServiceAddUrlBackendClient;
import io.github.xieyongxin.testing.addurl.service.DefaultAddUrlService;
import io.github.xieyongxin.testing.addurl.service.FileIndexStore;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;

/** AddUrl 命令行演示：默认执行一次完整 HTTP 请求，也可保持服务运行。 */
public final class AddUrlApplication {

    private AddUrlApplication() {
    }

    public static void main(String[] args) throws Exception {
        Path indexPath = Path.of(System.getProperty("java.io.tmpdir"), "addurl-demo-index.tsv");
        boolean serve = args.length == 2 && "--serve".equals(args[0]);
        int port = serve ? Integer.parseInt(args[1]) : 0;
        try (var store = new FileIndexStore(indexPath)) {
            var service = new DefaultAddUrlService(store);
            try (var frontend = new AddUrlFrontend(new ServiceAddUrlBackendClient(service),
                    Duration.ofSeconds(2));
                 var server = new AddUrlHttpServer(frontend, port)) {
                server.start();
                if (serve) {
                    System.out.println("AddUrl 服务已启动：" + server.endpoint());
                    Thread.currentThread().join();
                    return;
                }

                URI requestUri = URI.create(server.endpoint()
                        + "?url=https%3A%2F%2Fwww.example.com&comment=命令行演示");
                HttpRequest request = HttpRequest.newBuilder(requestUri).GET().build();
                HttpResponse<String> response = HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("HTTP " + response.statusCode() + "：" + response.body());
                System.out.println("索引文件：" + indexPath);
            }
        }
    }
}
