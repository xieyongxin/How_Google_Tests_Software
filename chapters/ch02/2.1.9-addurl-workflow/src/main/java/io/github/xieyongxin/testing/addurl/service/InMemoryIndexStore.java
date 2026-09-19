package io.github.xieyongxin.testing.addurl.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 无外部依赖的内存索引，主要用于小型和中型测试。 */
public final class InMemoryIndexStore implements IndexStore {

    private final Map<String, String> entries = new ConcurrentHashMap<>();

    @Override
    public void add(String url, String comment) throws IOException {
        entries.put(url, comment == null ? "" : comment);
    }

    @Override
    public boolean contains(String url) throws IOException {
        return entries.containsKey(url);
    }

    public String commentFor(String url) {
        return entries.get(url);
    }
}
