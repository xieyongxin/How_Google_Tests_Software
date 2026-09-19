package io.github.xieyongxin.testing.addurl.service;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("small")
class FileIndexStoreTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void writesAndReloadsEntriesWithSpecialCharacters() throws Exception {
        Path indexFile = temporaryDirectory.resolve("nested").resolve("index.tsv");
        String url = "https://example.com/a?x=1&y=中文";
        String comment = "备注\t含有换行\n";

        try (var store = new FileIndexStore(indexFile)) {
            store.add(url, comment);
        }

        try (var reloaded = new FileIndexStore(indexFile)) {
            assertTrue(reloaded.contains(url));
        }
        assertTrue(Files.size(indexFile) > 0);
    }
}
