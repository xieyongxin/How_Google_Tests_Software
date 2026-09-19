package io.github.xieyongxin.testing.fakes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.Test;

class FakeInventoryServiceTest {

    @Test
    void reservesAndReleasesAvailableStock() {
        FakeInventoryService inventory = new FakeInventoryService(Map.of("book-1", 3));

        assertTrue(inventory.reserve("book-1", 2));
        assertEquals(1, inventory.stockOf("book-1"));

        inventory.release("book-1", 2);
        assertEquals(3, inventory.stockOf("book-1"));
    }

    @Test
    void rejectsReservationWhenStockIsInsufficient() {
        FakeInventoryService inventory = new FakeInventoryService(Map.of("book-1", 1));

        assertFalse(inventory.reserve("book-1", 2));
        assertEquals(1, inventory.stockOf("book-1"));
    }
}

