package com.jewelry.inventory.entity;

import com.jewelry.inventory.enums.InventoryStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Inventory entity — owns stock data for a product.
 *
 * CONCURRENCY STRATEGY: Pessimistic locking chosen over optimistic locking.
 *
 * Rationale:
 * - Inventory operations (reserve, release, deduct) are write-heavy and
 *   contention is frequent under load (flash sales, popular items).
 * - Optimistic locking causes StaleObjectStateException on conflict,
 *   requiring application-level retry logic — more complex.
 * - Pessimistic locking (PESSIMISTIC_WRITE) acquires a DB-level row lock,
 *   ensuring only ONE transaction modifies stock at a time. The second
 *   request blocks briefly, then executes correctly.
 * - For an inventory service where correctness > throughput, this is the
 *   safer, simpler, and more interview-friendly choice.
 *
 * The @Lock annotation is applied at the repository query level, not here.
 */
@Entity
@Table(
    name = "inventory",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_inventory_product_id", columnNames = "product_id")
    },
    indexes = {
        @Index(name = "idx_inventory_sku", columnList = "sku"),
        @Index(name = "idx_inventory_status", columnList = "status"),
        @Index(name = "idx_inventory_available_qty", columnList = "available_quantity")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Foreign key to product in Product Service.
     * We store only the productId — not the full Product entity.
     * The Product Service owns all product details.
     */
    @Column(name = "product_id", nullable = false, unique = true)
    private Long productId;

    /**
     * SKU is stored for identification without calling Product Service.
     * Enables filtering and identification in inventory reports.
     */
    @Column(nullable = false, unique = true, length = 100)
    private String sku;

    /**
     * Currently available stock that can be reserved/purchased.
     * Business Rule: Must never be negative.
     */
    @Column(name = "available_quantity", nullable = false)
    @Builder.Default
    private Integer availableQuantity = 0;

    /**
     * Stock reserved by carts/pending orders, not yet deducted.
     * Business Rule: Must never exceed availableQuantity at reservation time.
     */
    @Column(name = "reserved_quantity", nullable = false)
    @Builder.Default
    private Integer reservedQuantity = 0;

    /**
     * Total units permanently sold (completed orders).
     * Business Rule: Incremented only on deduct; never decremented.
     */
    @Column(name = "sold_quantity", nullable = false)
    @Builder.Default
    private Integer soldQuantity = 0;

    /**
     * Threshold below which the item is considered "low stock".
     * Used by GET /admin/inventory/low-stock.
     */
    @Column(name = "low_stock_threshold", nullable = false)
    @Builder.Default
    private Integer lowStockThreshold = 5;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private InventoryStatus status = InventoryStatus.ACTIVE;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // -----------------------------------------------------------------------
    // Centralized status management — single source of truth
    // -----------------------------------------------------------------------

    /**
     * Recalculates status based on availableQuantity.
     * Called after every stock mutation to keep state consistent.
     * If status is INACTIVE, we leave it — admin must re-activate explicitly.
     */
    public void recalculateStatus() {
        if (this.status == InventoryStatus.INACTIVE) {
            return; // Admin-driven state; do not auto-change
        }
        this.status = (this.availableQuantity > 0) ? InventoryStatus.ACTIVE : InventoryStatus.OUT_OF_STOCK;
    }
}
