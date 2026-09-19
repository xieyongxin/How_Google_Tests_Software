package io.github.xieyongxin.testing.foundations;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Replaceable in-memory index for the disposable concept prototype. */
public final class InMemoryIndex {

    private final Map<String, String> entries = new LinkedHashMap<>();

    /** Adds or replaces a URL comment. */
    public void add(String url, String comment) {
        entries.put(url, comment == null ? "" : comment);
    }

    /** Checks whether the URL has been accepted. */
    public boolean contains(String url) {
        return entries.containsKey(url);
    }

    /** Returns the number of distinct URLs. */
    public int size() {
        return entries.size();
    }

    /** Exposes a read-only snapshot for diagnostics. */
    public Map<String, String> snapshot() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(entries));
    }
}

