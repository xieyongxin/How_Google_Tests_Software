package io.github.xieyongxin.testing.foundations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("small")
class AddUrlPrototypeTest {

    private InMemoryIndex index;
    private AddUrlPrototype prototype;

    @BeforeEach
    void setUp() {
        index = new InMemoryIndex();
        prototype = new AddUrlPrototype(new UrlValidator(), index);
    }

    @Test
    void acceptsHttpAndStoresItInTheIndex() {
        AddUrlResult result = prototype.validateAndAdd(" https://www.example.com ", "demo");

        assertTrue(result.accepted());
        assertEquals("https://www.example.com", result.normalizedUrl());
        assertTrue(index.contains("https://www.example.com"));
    }

    @Test
    void rejectsUnsupportedScheme() {
        AddUrlResult result = prototype.validateAndAdd("ftp://files.example.com", "demo");

        assertFalse(result.accepted());
        assertEquals(0, index.size());
    }

    @Test
    void rejectsMissingHostAndDoesNotPolluteIndex() {
        AddUrlResult result = prototype.validateAndAdd("https:///missing-host", "demo");

        assertFalse(result.accepted());
        assertEquals(0, index.size());
    }

    @Test
    void rejectsBlankInput() {
        AddUrlResult result = prototype.validateAndAdd("  ", "demo");

        assertFalse(result.accepted());
        assertEquals(0, index.size());
    }

    @Test
    void duplicateUrlDoesNotCreateAnotherIndexEntry() {
        prototype.validateAndAdd("https://www.example.com", "first");
        prototype.validateAndAdd("https://www.example.com", "second");

        assertEquals(1, index.size());
    }
}
