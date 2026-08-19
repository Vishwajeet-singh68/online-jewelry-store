package com.jewelry.inventory.enums;

/**
 * Represents the lifecycle state of an inventory record.
 *
 * ACTIVE      - product has stock available (availableQuantity > 0)
 * OUT_OF_STOCK - product has zero available quantity
 * INACTIVE    - product has been manually deactivated by admin
 */
public enum InventoryStatus {
    ACTIVE,
    INACTIVE,
    OUT_OF_STOCK
}
