package io.github.xieyongxin.testing.addurl;

import com.google.protobuf.CodedOutputStream;
import io.github.xieyongxin.testing.addurl.protocol.AddUrlProto;
import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Tag("small")
class ProtocolCompatibilityTest {

    @Test
    void newReaderAcceptsOldRequestWithoutOptionalComment() throws Exception {
        var oldMessage = AddUrlProto.AddUrlRequest.newBuilder()
                .setUrl("https://legacy.example")
                .build()
                .toByteArray();

        var parsed = AddUrlProto.AddUrlRequest.parseFrom(oldMessage);

        assertEquals("https://legacy.example", parsed.getUrl());
        assertFalse(parsed.hasComment());
    }

    @Test
    void newReaderIgnoresAFieldAddedByFutureWriter() throws Exception {
        var bytes = new ByteArrayOutputStream();
        CodedOutputStream output = CodedOutputStream.newInstance(bytes);
        output.writeString(1, "https://future.example");
        output.writeString(3, "future metadata");
        output.flush();

        var parsed = AddUrlProto.AddUrlRequest.parseFrom(bytes.toByteArray());

        assertEquals("https://future.example", parsed.getUrl());
    }
}
