package io.github.xieyongxin.testing.addurl.frontend;

import io.github.xieyongxin.testing.addurl.protocol.AddUrlProto;
import io.github.xieyongxin.testing.addurl.service.AddUrlReplies;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/** 解析查询参数、调用后端并将协议错误映射为客户端响应。 */
public final class AddUrlFrontend implements AutoCloseable {

    private final AddUrlBackendClient backendClient;
    private final Duration timeout;
    private final ExecutorService executor;

    public AddUrlFrontend(AddUrlBackendClient backendClient, Duration timeout) {
        if (timeout == null || timeout.isNegative() || timeout.isZero()) {
            throw new IllegalArgumentException("timeout 必须为正数");
        }
        this.backendClient = backendClient;
        this.timeout = timeout;
        this.executor = Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable, "addurl-frontend");
            thread.setDaemon(true);
            return thread;
        });
    }

    public FrontendResponse handleQuery(String rawQuery) {
        Map<String, String> parameters;
        try {
            parameters = parseQuery(rawQuery);
        } catch (IllegalArgumentException exception) {
            return clientError("查询参数编码无效");
        }

        String url = parameters.get("url");
        if (url == null || url.isBlank()) {
            return clientError("缺少 url 参数");
        }

        AddUrlProto.AddUrlRequest.Builder request = AddUrlProto.AddUrlRequest.newBuilder()
                .setUrl(url);
        if (parameters.containsKey("comment")) {
            request.setComment(parameters.get("comment"));
        }

        CompletableFuture<AddUrlProto.AddUrlReply> call = CompletableFuture.supplyAsync(() -> {
            try {
                return backendClient.add(request.build());
            } catch (Exception exception) {
                throw new CompletionException(exception);
            }
        }, executor);

        try {
            AddUrlProto.AddUrlReply reply = call.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
            return mapReply(reply);
        } catch (TimeoutException exception) {
            call.cancel(true);
            return new FrontendResponse(AddUrlReplies.TIMEOUT, "后端请求超时", AddUrlReplies.error(
                    AddUrlReplies.TIMEOUT, "后端请求超时"));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return backendError("请求被中断");
        } catch (ExecutionException exception) {
            return backendError("后端调用失败");
        }
    }

    private static FrontendResponse mapReply(AddUrlProto.AddUrlReply reply) {
        if (reply == null) {
            return backendError("后端返回为空");
        }
        if (!reply.hasErrorCode()) {
            return new FrontendResponse(200, "添加成功", reply);
        }
        int code = reply.getErrorCode();
        int status = code >= 400 && code < 600 ? code : AddUrlReplies.BACKEND_ERROR;
        String details = reply.hasErrorDetails() ? reply.getErrorDetails() : "后端返回错误";
        return new FrontendResponse(status, details, reply);
    }

    private static FrontendResponse clientError(String details) {
        return new FrontendResponse(AddUrlReplies.CLIENT_ERROR, details,
                AddUrlReplies.error(AddUrlReplies.CLIENT_ERROR, details));
    }

    private static FrontendResponse backendError(String details) {
        return new FrontendResponse(AddUrlReplies.BACKEND_ERROR, details,
                AddUrlReplies.error(AddUrlReplies.BACKEND_ERROR, details));
    }

    static Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> parameters = new LinkedHashMap<>();
        if (rawQuery == null || rawQuery.isBlank()) {
            return parameters;
        }
        for (String pair : rawQuery.split("&", -1)) {
            if (pair.isEmpty()) {
                continue;
            }
            int separator = pair.indexOf('=');
            String encodedName = separator < 0 ? pair : pair.substring(0, separator);
            String encodedValue = separator < 0 ? "" : pair.substring(separator + 1);
            String name = decode(encodedName);
            String value = decode(encodedValue);
            parameters.put(name, value);
        }
        return parameters;
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    @Override
    public void close() {
        executor.shutdownNow();
    }
}
