package com.jewelry.inventory.repository;

import com.jewelry.inventory.entity.Inventory;
import com.jewelry.inventory.enums.InventoryStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository for Inventory entity.
 *
 * The PESSIMISTIC_WRITE lock on findByProductIdWithLock is the key to
 * concurrency safety. When the inventory service reserves/releases/deducts
 * stock, it acquires this lock so only one transaction can modify a row at
 * a time. The second concurrent transaction waits at the DB level.
 */
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(Long productId);

    boolean existsByProductId(Long productId);

    boolean existsBySku(String sku);

    /**
     * Acquires a pessimistic write lock on the row.
     * Used exclusively by stock-mutation operations (reserve, release, deduct, adjust).
     * READ operations use the unlocked findByProductId.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.productId = :productId")
    Optional<Inventory> findByProductIdWithLock(@Param("productId") Long productId);

    Page<Inventory> findAllByStatus(InventoryStatus status, Pageable pageable);

    /**
     * Low-stock query: items where available quantity <= threshold.
     * Excludes INACTIVE items from low-stock alerts.
     */
    @Query("SELECT i FROM Inventory i WHERE i.availableQuantity <= i.lowStockThreshold AND i.status <> 'INACTIVE'")
    Page<Inventory> findLowStockItems(Pageable pageable);
}
