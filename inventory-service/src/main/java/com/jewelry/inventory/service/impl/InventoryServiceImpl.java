package com.jewelry.inventory.service.impl;

import com.jewelry.inventory.dto.request.CreateInventoryRequest;
import com.jewelry.inventory.dto.request.StockAdjustmentRequest;
import com.jewelry.inventory.dto.request.StockQuantityRequest;
import com.jewelry.inventory.dto.request.UpdateInventoryRequest;
import com.jewelry.inventory.dto.response.InventoryResponse;
import com.jewelry.inventory.dto.response.PageResponse;
import com.jewelry.inventory.entity.Inventory;
import com.jewelry.inventory.enums.InventoryStatus;
import com.jewelry.inventory.exception.DuplicateInventoryException;
import com.jewelry.inventory.exception.InsufficientStockException;
import com.jewelry.inventory.exception.InvalidStockOperationException;
import com.jewelry.inventory.exception.InventoryNotFoundException;
import com.jewelry.inventory.mapper.InventoryMapper;
import com.jewelry.inventory.repository.InventoryRepository;
import com.jewelry.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private static final int MAX_PAGE_SIZE = 100;

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    // =========================================================================
    // Admin operations
    // =========================================================================

    @Override
    @Transactional
    public InventoryResponse createInventory(CreateInventoryRequest request) {
        // Enforce one inventory record per product
        if (inventoryRepository.existsByProductId(request.getProductId())) {
            throw new DuplicateInventoryException(
                    "Inventory already exists for product ID: " + request.getProductId());
        }

        if (inventoryRepository.existsBySku(request.getSku())) {
            throw new DuplicateInventoryException(
                    "Inventory already exists for SKU: " + request.getSku());
        }

        Inventory inventory = inventoryMapper.toEntity(request);

        // Set threshold with a default if not provided
        if (request.getLowStockThreshold() != null) {
            inventory.setLowStockThreshold(request.getLowStockThreshold());
        } else {
            inventory.setLowStockThreshold(5);
        }

        // Determine initial status based on quantity
        inventory.recalculateStatus();

        Inventory saved = inventoryRepository.save(inventory);
        log.info("Inventory created for productId={}, sku={}, quantity={}",
                saved.getProductId(), saved.getSku(), saved.getAvailableQuantity());
        return inventoryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public InventoryResponse updateInventory(Long productId, UpdateInventoryRequest request) {
        Inventory inventory = getInventoryEntityByProductId(productId);

        if (request.getSku() != null && !request.getSku().isBlank()) {
            inventory.setSku(request.getSku());
        }
        if (request.getLowStockThreshold() != null) {
            inventory.setLowStockThreshold(request.getLowStockThreshold());
        }
        if (request.getStatus() != null) {
            // Admin can explicitly set any status (e.g., to INACTIVE to retire a product)
            inventory.setStatus(request.getStatus());
        }

        Inventory updated = inventoryRepository.save(inventory);
        return inventoryMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public InventoryResponse adjustStock(Long productId, StockAdjustmentRequest request) {
        // Use pessimistic lock — adjustment modifies stock
        Inventory inventory = inventoryRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for product ID: " + productId));

        int adjustment = request.getQuantity();
        int newQuantity = inventory.getAvailableQuantity() + adjustment;

        if (newQuantity < 0) {
            throw new InvalidStockOperationException(
                    "Stock adjustment would result in negative quantity. " +
                    "Current: " + inventory.getAvailableQuantity() + ", Adjustment: " + adjustment);
        }

        inventory.setAvailableQuantity(newQuantity);
        inventory.recalculateStatus();

        Inventory updated = inventoryRepository.save(inventory);
        log.info("Stock adjusted for productId={}, reason={}, delta={}, newQuantity={}",
                productId, request.getReason(), adjustment, newQuantity);
        return inventoryMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InventoryResponse> getAllInventory(int page, int size) {
        Pageable pageable = buildPageable(page, size);
        Page<InventoryResponse> result = inventoryRepository.findAll(pageable)
                .map(inventoryMapper::toResponse);
        return PageResponse.from(result);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InventoryResponse> getLowStockInventory(int page, int size) {
        Pageable pageable = buildPageable(page, size);
        Page<InventoryResponse> result = inventoryRepository.findLowStockItems(pageable)
                .map(inventoryMapper::toResponse);
        return PageResponse.from(result);
    }

    // =========================================================================
    // Read operations
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getInventoryByProductId(Long productId) {
        return inventoryMapper.toResponse(getInventoryEntityByProductId(productId));
    }

    // =========================================================================
    // Stock lifecycle operations — CONCURRENCY-CRITICAL
    // All use pessimistic write lock to prevent race conditions.
    // =========================================================================

    /**
     * Reserve stock for a pending order.
     *
     * Flow:
     *  1. Acquire PESSIMISTIC_WRITE lock on the inventory row
     *  2. Check sufficient available stock
     *  3. Decrement availableQuantity, increment reservedQuantity
     *  4. Recalculate status (may become OUT_OF_STOCK)
     *  5. Commit atomically
     *
     * Concurrency guarantee:
     *  If two requests arrive simultaneously for the last unit,
     *  only the first to acquire the lock succeeds. The second waits,
     *  then finds availableQuantity=0 and throws InsufficientStockException.
     */
    @Override
    @Transactional
    public InventoryResponse reserveStock(Long productId, StockQuantityRequest request) {
        Inventory inventory = inventoryRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for product ID: " + productId));

        validateActiveInventory(inventory, "reserve");

        int quantity = request.getQuantity();

        if (inventory.getAvailableQuantity() < quantity) {
            throw new InsufficientStockException(
                    "Insufficient stock available. Requested: " + quantity +
                    ", Available: " + inventory.getAvailableQuantity());
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);
        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
        inventory.recalculateStatus();

        Inventory updated = inventoryRepository.save(inventory);
        log.info("Stock reserved for productId={}, quantity={}, remaining={}",
                productId, quantity, updated.getAvailableQuantity());
        return inventoryMapper.toResponse(updated);
    }

    /**
     * Release previously reserved stock back to available.
     * Called when an order is cancelled before completion.
     *
     * Business Rule: Cannot release more than currently reserved.
     */
    @Override
    @Transactional
    public InventoryResponse releaseStock(Long productId, StockQuantityRequest request) {
        Inventory inventory = inventoryRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for product ID: " + productId));

        int quantity = request.getQuantity();

        if (inventory.getReservedQuantity() < quantity) {
            throw new InvalidStockOperationException(
                    "Cannot release more than reserved. Requested release: " + quantity +
                    ", Currently reserved: " + inventory.getReservedQuantity());
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);
        inventory.recalculateStatus();

        Inventory updated = inventoryRepository.save(inventory);
        log.info("Stock released for productId={}, quantity={}, available={}",
                productId, quantity, updated.getAvailableQuantity());
        return inventoryMapper.toResponse(updated);
    }

    /**
     * Permanently deduct reserved stock after a successful order.
     *
     * Before: available=8, reserved=2, sold=10
     * After:  available=8, reserved=0, sold=12
     *
     * This does NOT touch available quantity — the stock was already
     * moved from available→reserved during the reserve step.
     *
     * Business Rule: Cannot deduct more than currently reserved.
     */
    @Override
    @Transactional
    public InventoryResponse deductStock(Long productId, StockQuantityRequest request) {
        Inventory inventory = inventoryRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for product ID: " + productId));

        int quantity = request.getQuantity();

        if (inventory.getReservedQuantity() < quantity) {
            throw new InvalidStockOperationException(
                    "Cannot deduct more than reserved. Requested deduction: " + quantity +
                    ", Currently reserved: " + inventory.getReservedQuantity());
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        inventory.setSoldQuantity(inventory.getSoldQuantity() + quantity);
        // Note: availableQuantity is not changed — already deducted during reserve

        Inventory updated = inventoryRepository.save(inventory);
        log.info("Stock deducted for productId={}, quantity={}, totalSold={}",
                productId, quantity, updated.getSoldQuantity());
        return inventoryMapper.toResponse(updated);
    }

    // =========================================================================
    // Private helpers
    // =========================================================================

    private Inventory getInventoryEntityByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for product ID: " + productId));
    }

    private void validateActiveInventory(Inventory inventory, String operation) {
        if (inventory.getStatus() == InventoryStatus.INACTIVE) {
            throw new InvalidStockOperationException(
                    "Cannot " + operation + " stock for an INACTIVE inventory (productId=" + inventory.getProductId() + ")");
        }
    }

    private Pageable buildPageable(int page, int size) {
        int validPage = Math.max(page, 0);
        int validSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        return PageRequest.of(validPage, validSize, Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
