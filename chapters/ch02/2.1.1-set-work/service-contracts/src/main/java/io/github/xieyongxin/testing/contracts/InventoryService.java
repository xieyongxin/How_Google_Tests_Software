package io.github.xieyongxin.testing.contracts;

/** Contract used by order processing to reserve and release product stock. */
public interface InventoryService {

    /**
     * Reserves stock for an order.
     *
     * @param productId product identifier
     * @param quantity requested quantity
     * @return {@code true} when the requested stock was reserved
     */
    boolean reserve(String productId, int quantity);

    /**
     * Returns a previous reservation to stock.
     *
     * @param productId product identifier
     * @param quantity quantity to release
     */
    void release(String productId, int quantity);
}

