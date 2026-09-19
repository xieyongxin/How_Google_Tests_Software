package io.github.xieyongxin.testing.addurl.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 使用逐行 Base64 文件模拟持久化索引。 */
public final class FileIndexStore implements IndexStore {

    private final Path file;
    private final Map<String, String> entries = new ConcurrentHashMap<>();

    public FileIndexStore(Path file) throws IOException {
        this.file = file;
        if (Files.exists(file)) {
            for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                if (line.isBlank()) {
                    continue;
                }
                String[] fields = line.split("\\t", -1);
                if (fields.length == 2) {
                    String url = decode(fields[0]);
                    String comment = decode(fields[1]);
                    entries.put(url, comment);
                }
            }
        } else if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
    }

    @Override
    public synchronized void add(String url, String comment) throws IOException {
        String safeComment = comment == null ? "" : comment;
        entries.put(url, safeComment);
        String line = encode(url) + "\t" + encode(safeComment) + System.lineSeparator();
        Files.writeString(file, line, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    @Override
    public boolean contains(String url) throws IOException {
        return entries.containsKey(url);
    }

    private static String encode(String value) {
        return Base64.getUrlEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
