package io.github.xieyongxin.testing.addurl;

import io.github.xieyongxin.testing.addurl.frontend.AddUrlFrontend;
import io.github.xieyongxin.testing.addurl.frontend.ServiceAddUrlBackendClient;
import io.github.xieyongxin.testing.addurl.service.DefaultAddUrlService;
import io.github.xieyongxin.testing.addurl.service.FileIndexStore;
import java.nio.file.Path;
import java.time.Duration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("large")
class FileIndexStoreIT {

    @TempDir
    Path temporaryDirectory;

    @Test
    void dataSurvivesStoreReopen() throws Exception {
        Path indexPath = temporaryDirectory.resolve("index.tsv");
        try (var store = new FileIndexStore(indexPath);
             var frontend = new AddUrlFrontend(
                     new ServiceAddUrlBackendClient(new DefaultAddUrlService(store)),
                     Duration.ofSeconds(1))) {
            assertEquals(200, frontend.handleQuery(
                    "url=https%3A%2F%2Fpersist.example&comment=persisted").statusCode());
        }

        try (var reopened = new FileIndexStore(indexPath)) {
            assertTrue(reopened.contains("https://persist.example"));
        }
    }
}
