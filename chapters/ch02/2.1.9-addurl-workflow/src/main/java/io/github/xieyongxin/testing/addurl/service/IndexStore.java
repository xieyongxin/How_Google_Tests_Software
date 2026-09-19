package io.github.xieyongxin.testing.addurl.service;

import java.io.IOException;

/** AddUrl 的索引存储抽象，便于服务与具体存储解耦。 */
public interface IndexStore extends AutoCloseable {

    void add(String url, String comment) throws IOException;

    boolean contains(String url) throws IOException;

    @Override
    default void close() throws IOException {
        // 内存实现没有需要释放的资源。
    }
}
