package io.github.xieyongxin.testing.fakes;

import io.github.xieyongxin.testing.contracts.InventoryService;
import java.util.HashMap;
import java.util.Map;

/** In-memory inventory for tests and local development. */
public final class FakeInventoryService implements InventoryService {

    private final Map<String, Integer> stock = new HashMap<>();

    public FakeInventoryService(Map<String, Integer> initialStock) {
        initialStock.forEach(this::putInitialStock);
    }

    @Override
    public synchronized boolean reserve(String productId, int quantity) {
        int available = stock.getOrDefault(productId, 0);
        if (quantity <= 0 || available < quantity) {
            return false;
        }
        stock.put(productId, available - quantity);
        return true;
    }

    @Override
    public synchronized void release(String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Release quantity must be positive");
        }
        stock.merge(productId, quantity, Integer::sum);
    }

    /** Returns the currently available quantity for assertions or diagnostics. */
    public synchronized int stockOf(String productId) {
        return stock.getOrDefault(productId, 0);
    }

    private void putInitialStock(String productId, int quantity) {
        if (productId == null || productId.isBlank() || quantity < 0) {
            throw new IllegalArgumentException("Initial stock must have a product and nonnegative quantity");
        }
        stock.put(productId, quantity);
    }
}

