package io.github.xieyongxin.testing.addurl;

import io.github.xieyongxin.testing.addurl.protocol.AddUrlProto;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("small")
class ProtocolDefinitionTest {

    @Test
    void requestFieldsKeepStableNumbers() {
        var descriptor = AddUrlProto.AddUrlRequest.getDescriptor();

        assertEquals(1, descriptor.findFieldByName("url").getNumber());
        assertEquals(2, descriptor.findFieldByName("comment").getNumber());
        assertTrue(descriptor.findFieldByName("url").isRequired());
    }

    @Test
    void generatedTypesRoundTripMessages() throws Exception {
        var request = AddUrlProto.AddUrlRequest.newBuilder()
                .setUrl("https://example.com")
                .setComment("协议测试")
                .build();

        var parsed = AddUrlProto.AddUrlRequest.parseFrom(request.toByteArray());

        assertEquals("https://example.com", parsed.getUrl());
        assertEquals("协议测试", parsed.getComment());
    }
}
