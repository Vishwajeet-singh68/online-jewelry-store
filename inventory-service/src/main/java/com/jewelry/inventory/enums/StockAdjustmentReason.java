package com.jewelry.inventory.enums;

/**
 * Reasons for a stock adjustment operation.
 *
 * RESTOCK    - new stock received from supplier (positive adjustment)
 * DAMAGE     - stock written off due to damage (negative adjustment)
 * CORRECTION - inventory count correction (can be positive or negative)
 * RETURN     - customer return restoring stock (positive adjustment)
 * OTHER      - any other reason (must include a note)
 */
public enum StockAdjustmentReason {
    RESTOCK,
    DAMAGE,
    CORRECTION,
    RETURN,
    OTHER
}
