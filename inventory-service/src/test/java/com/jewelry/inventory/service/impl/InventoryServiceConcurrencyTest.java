package com.jewelry.inventory.service.impl;

import com.jewelry.inventory.dto.request.CreateInventoryRequest;
import com.jewelry.inventory.dto.request.StockQuantityRequest;
import com.jewelry.inventory.entity.Inventory;
import com.jewelry.inventory.enums.InventoryStatus;
import com.jewelry.inventory.exception.InsufficientStockException;
import com.jewelry.inventory.mapper.InventoryMapper;
import com.jewelry.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Concurrency Tests for Inventory Service.
 *
 * These tests verify the pessimistic locking strategy prevents overselling.
 *
 * Test scenario:
 *   Initial stock = 1
 *   Two concurrent requests each try to reserve 1 unit.
 *   Expected: exactly 1 succeeds, 1 fails with InsufficientStockException.
 *   Final state: availableQuantity=0, reservedQuantity=1 — NEVER -1.
 *
 * Note: These tests require a running database (use integration test setup).
 * Annotate with @SpringBootTest to get a real application context with transactions.
 * In CI, use Testcontainers (see InventoryIntegrationTest) for DB isolation.
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Inventory Concurrency Tests")
class InventoryServiceConcurrencyTest {

    @Autowired
    private InventoryServiceImpl inventoryService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryMapper inventoryMapper;

    private Long testProductId = 999L;

    @BeforeEach
    void setUp() {
        inventoryRepository.findByProductId(testProductId)
                .ifPresent(inv -> inventoryRepository.delete(inv));

        Inventory inventory = Inventory.builder()
                .productId(testProductId)
                .sku("TEST-CONCURRENCY-SKU")
                .availableQuantity(1)  // Only 1 unit!
                .reservedQuantity(0)
                .soldQuantity(0)
                .lowStockThreshold(0)
                .status(InventoryStatus.ACTIVE)
                .build();
        inventoryRepository.save(inventory);
    }

    @Test
    @DisplayName("CRITICAL: Two concurrent reserve requests for last unit — only one should succeed")
    void concurrentReserve_onlyOneSucceeds() throws InterruptedException {
        int threadCount = 2;
        StockQuantityRequest request = new StockQuantityRequest(1);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1); // all threads start simultaneously
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        List<Future<Void>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> {
                startLatch.await(); // wait for the gun
                try {
                    inventoryService.reserveStock(testProductId, request);
                    successCount.incrementAndGet();
                } catch (InsufficientStockException e) {
                    failureCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
                return null;
            }));
        }

        startLatch.countDown(); // fire!
        doneLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Verify exactly one succeeded and one failed
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failureCount.get()).isEqualTo(1);

        // Verify final inventory state
        Inventory finalState = inventoryRepository.findByProductId(testProductId).orElseThrow();
        assertThat(finalState.getAvailableQuantity()).isEqualTo(0);
        assertThat(finalState.getReservedQuantity()).isEqualTo(1);
        // CRITICAL: availableQuantity must NEVER be negative
        assertThat(finalState.getAvailableQuantity()).isGreaterThanOrEqualTo(0);
        assertThat(finalState.getStatus()).isEqualTo(InventoryStatus.OUT_OF_STOCK);
    }

    @Test
    @DisplayName("Five concurrent reserve requests for 3 units — exactly 3 succeed")
    void concurrentReserve_multipleRequests() throws InterruptedException {
        // Reset to 3 units
        inventoryRepository.findByProductId(testProductId)
                .ifPresent(inv -> {
                    inv.setAvailableQuantity(3);
                    inv.setReservedQuantity(0);
                    inv.setStatus(InventoryStatus.ACTIVE);
                    inventoryRepository.save(inv);
                });

        int threadCount = 5;
        StockQuantityRequest request = new StockQuantityRequest(1);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    inventoryService.reserveStock(testProductId, request);
                    successCount.incrementAndGet();
                } catch (InsufficientStockException e) {
                    failureCount.incrementAndGet();
                } catch (Exception ignored) {
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(successCount.get()).isEqualTo(3);
        assertThat(failureCount.get()).isEqualTo(2);

        Inventory finalState = inventoryRepository.findByProductId(testProductId).orElseThrow();
        assertThat(finalState.getAvailableQuantity()).isEqualTo(0);
        assertThat(finalState.getReservedQuantity()).isEqualTo(3);
        // Stock invariant: available + reserved should equal original stock
        assertThat(finalState.getAvailableQuantity() + finalState.getReservedQuantity()).isEqualTo(3);
    }
}
