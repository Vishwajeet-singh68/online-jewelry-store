package com.jewelry.inventory.service.impl;

import com.jewelry.inventory.dto.request.CreateInventoryRequest;
import com.jewelry.inventory.dto.request.StockAdjustmentRequest;
import com.jewelry.inventory.dto.request.StockQuantityRequest;
import com.jewelry.inventory.dto.request.UpdateInventoryRequest;
import com.jewelry.inventory.dto.response.InventoryResponse;
import com.jewelry.inventory.entity.Inventory;
import com.jewelry.inventory.enums.InventoryStatus;
import com.jewelry.inventory.enums.StockAdjustmentReason;
import com.jewelry.inventory.exception.DuplicateInventoryException;
import com.jewelry.inventory.exception.InsufficientStockException;
import com.jewelry.inventory.exception.InvalidStockOperationException;
import com.jewelry.inventory.exception.InventoryNotFoundException;
import com.jewelry.inventory.mapper.InventoryMapper;
import com.jewelry.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InventoryServiceImpl Unit Tests")
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMapper inventoryMapper;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Inventory sampleInventory;
    private InventoryResponse sampleResponse;

    @BeforeEach
    void setUp() {
        sampleInventory = Inventory.builder()
                .id(1L)
                .productId(101L)
                .sku("JW-RING-18K-001")
                .availableQuantity(10)
                .reservedQuantity(0)
                .soldQuantity(0)
                .lowStockThreshold(3)
                .status(InventoryStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleResponse = InventoryResponse.builder()
                .id(1L)
                .productId(101L)
                .sku("JW-RING-18K-001")
                .availableQuantity(10)
                .reservedQuantity(0)
                .soldQuantity(0)
                .lowStockThreshold(3)
                .status(InventoryStatus.ACTIVE)
                .build();
    }

    // =========================================================================
    // CREATE INVENTORY
    // =========================================================================
    @Nested
    @DisplayName("Create Inventory")
    class CreateInventoryTests {

        @Test
        @DisplayName("Should create inventory successfully")
        void createInventory_success() {
            CreateInventoryRequest request = CreateInventoryRequest.builder()
                    .productId(101L).sku("JW-RING-18K-001").quantity(10).lowStockThreshold(3).build();

            when(inventoryRepository.existsByProductId(101L)).thenReturn(false);
            when(inventoryRepository.existsBySku("JW-RING-18K-001")).thenReturn(false);
            when(inventoryMapper.toEntity(request)).thenReturn(sampleInventory);
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(sampleInventory);
            when(inventoryMapper.toResponse(sampleInventory)).thenReturn(sampleResponse);

            InventoryResponse result = inventoryService.createInventory(request);

            assertThat(result).isNotNull();
            assertThat(result.getProductId()).isEqualTo(101L);
            assertThat(result.getAvailableQuantity()).isEqualTo(10);
            verify(inventoryRepository).save(any(Inventory.class));
        }

        @Test
        @DisplayName("Should throw DuplicateInventoryException when product already has inventory")
        void createInventory_duplicateProduct_throws() {
            CreateInventoryRequest request = CreateInventoryRequest.builder()
                    .productId(101L).sku("JW-RING-18K-001").quantity(10).build();

            when(inventoryRepository.existsByProductId(101L)).thenReturn(true);

            assertThatThrownBy(() -> inventoryService.createInventory(request))
                    .isInstanceOf(DuplicateInventoryException.class)
                    .hasMessageContaining("101");

            verify(inventoryRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw DuplicateInventoryException when SKU already exists")
        void createInventory_duplicateSku_throws() {
            CreateInventoryRequest request = CreateInventoryRequest.builder()
                    .productId(102L).sku("JW-RING-18K-001").quantity(5).build();

            when(inventoryRepository.existsByProductId(102L)).thenReturn(false);
            when(inventoryRepository.existsBySku("JW-RING-18K-001")).thenReturn(true);

            assertThatThrownBy(() -> inventoryService.createInventory(request))
                    .isInstanceOf(DuplicateInventoryException.class);

            verify(inventoryRepository, never()).save(any());
        }
    }

    // =========================================================================
    // RESERVE STOCK
    // =========================================================================
    @Nested
    @DisplayName("Reserve Stock")
    class ReserveStockTests {

        @Test
        @DisplayName("Should reserve stock successfully")
        void reserveStock_success() {
            StockQuantityRequest request = new StockQuantityRequest(2);

            when(inventoryRepository.findByProductIdWithLock(101L)).thenReturn(Optional.of(sampleInventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(sampleInventory);
            when(inventoryMapper.toResponse(any(Inventory.class))).thenReturn(sampleResponse);

            inventoryService.reserveStock(101L, request);

            // Verify quantity mutation
            assertThat(sampleInventory.getAvailableQuantity()).isEqualTo(8);
            assertThat(sampleInventory.getReservedQuantity()).isEqualTo(2);
        }

        @Test
        @DisplayName("Should throw InsufficientStockException when not enough stock")
        void reserveStock_insufficientStock_throws() {
            StockQuantityRequest request = new StockQuantityRequest(15); // more than available

            when(inventoryRepository.findByProductIdWithLock(101L)).thenReturn(Optional.of(sampleInventory));

            assertThatThrownBy(() -> inventoryService.reserveStock(101L, request))
                    .isInstanceOf(InsufficientStockException.class)
                    .hasMessageContaining("Insufficient stock");

            // Verify stock was NOT modified
            assertThat(sampleInventory.getAvailableQuantity()).isEqualTo(10);
            assertThat(sampleInventory.getReservedQuantity()).isEqualTo(0);
            verify(inventoryRepository, never()).save(any());
        }

        @Test
        @DisplayName("Available quantity should not go negative after reserve")
        void reserveStock_quantityNeverNegative() {
            sampleInventory.setAvailableQuantity(1);
            StockQuantityRequest request = new StockQuantityRequest(2);

            when(inventoryRepository.findByProductIdWithLock(101L)).thenReturn(Optional.of(sampleInventory));

            assertThatThrownBy(() -> inventoryService.reserveStock(101L, request))
                    .isInstanceOf(InsufficientStockException.class);

            // Critical: available must NOT become negative
            assertThat(sampleInventory.getAvailableQuantity()).isGreaterThanOrEqualTo(0);
        }

        @Test
        @DisplayName("Status should become OUT_OF_STOCK when last unit is reserved")
        void reserveStock_lastUnit_becomesOutOfStock() {
            sampleInventory.setAvailableQuantity(1);
            StockQuantityRequest request = new StockQuantityRequest(1);

            Inventory savedInventory = Inventory.builder()
                    .id(1L).productId(101L).sku("JW-RING-18K-001")
                    .availableQuantity(0).reservedQuantity(1).soldQuantity(0)
                    .lowStockThreshold(3).status(InventoryStatus.OUT_OF_STOCK).build();

            InventoryResponse outOfStockResponse = InventoryResponse.builder()
                    .status(InventoryStatus.OUT_OF_STOCK).availableQuantity(0).reservedQuantity(1).build();

            when(inventoryRepository.findByProductIdWithLock(101L)).thenReturn(Optional.of(sampleInventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(savedInventory);
            when(inventoryMapper.toResponse(any(Inventory.class))).thenReturn(outOfStockResponse);

            InventoryResponse result = inventoryService.reserveStock(101L, request);

            assertThat(sampleInventory.getAvailableQuantity()).isEqualTo(0);
            assertThat(sampleInventory.getStatus()).isEqualTo(InventoryStatus.OUT_OF_STOCK);
        }

        @Test
        @DisplayName("Should throw InventoryNotFoundException for unknown product")
        void reserveStock_notFound_throws() {
            when(inventoryRepository.findByProductIdWithLock(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> inventoryService.reserveStock(999L, new StockQuantityRequest(1)))
                    .isInstanceOf(InventoryNotFoundException.class);
        }
    }

    // =========================================================================
    // RELEASE STOCK
    // =========================================================================
    @Nested
    @DisplayName("Release Stock")
    class ReleaseStockTests {

        @BeforeEach
        void setUpReserved() {
            sampleInventory.setAvailableQuantity(8);
            sampleInventory.setReservedQuantity(2);
        }

        @Test
        @DisplayName("Should release stock successfully")
        void releaseStock_success() {
            StockQuantityRequest request = new StockQuantityRequest(2);

            when(inventoryRepository.findByProductIdWithLock(101L)).thenReturn(Optional.of(sampleInventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(sampleInventory);
            when(inventoryMapper.toResponse(any(Inventory.class))).thenReturn(sampleResponse);

            inventoryService.releaseStock(101L, request);

            assertThat(sampleInventory.getAvailableQuantity()).isEqualTo(10);
            assertThat(sampleInventory.getReservedQuantity()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should throw InvalidStockOperationException when releasing more than reserved")
        void releaseStock_moreThanReserved_throws() {
            StockQuantityRequest request = new StockQuantityRequest(5); // reserved is only 2

            when(inventoryRepository.findByProductIdWithLock(101L)).thenReturn(Optional.of(sampleInventory));

            assertThatThrownBy(() -> inventoryService.releaseStock(101L, request))
                    .isInstanceOf(InvalidStockOperationException.class)
                    .hasMessageContaining("Cannot release more than reserved");

            verify(inventoryRepository, never()).save(any());
        }
    }

    // =========================================================================
    // DEDUCT STOCK
    // =========================================================================
    @Nested
    @DisplayName("Deduct Stock")
    class DeductStockTests {

        @BeforeEach
        void setUpReserved() {
            sampleInventory.setAvailableQuantity(8);
            sampleInventory.setReservedQuantity(2);
            sampleInventory.setSoldQuantity(10);
        }

        @Test
        @DisplayName("Should deduct stock successfully — reserved decreases, sold increases")
        void deductStock_success() {
            StockQuantityRequest request = new StockQuantityRequest(2);

            when(inventoryRepository.findByProductIdWithLock(101L)).thenReturn(Optional.of(sampleInventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(sampleInventory);
            when(inventoryMapper.toResponse(any(Inventory.class))).thenReturn(sampleResponse);

            inventoryService.deductStock(101L, request);

            // Available is unchanged (already moved to reserved during reserve)
            assertThat(sampleInventory.getAvailableQuantity()).isEqualTo(8);
            assertThat(sampleInventory.getReservedQuantity()).isEqualTo(0);
            assertThat(sampleInventory.getSoldQuantity()).isEqualTo(12);
        }

        @Test
        @DisplayName("Should throw InvalidStockOperationException when deducting more than reserved")
        void deductStock_moreThanReserved_throws() {
            StockQuantityRequest request = new StockQuantityRequest(5); // reserved is only 2

            when(inventoryRepository.findByProductIdWithLock(101L)).thenReturn(Optional.of(sampleInventory));

            assertThatThrownBy(() -> inventoryService.deductStock(101L, request))
                    .isInstanceOf(InvalidStockOperationException.class)
                    .hasMessageContaining("Cannot deduct more than reserved");

            // Quantities unchanged
            assertThat(sampleInventory.getReservedQuantity()).isEqualTo(2);
            assertThat(sampleInventory.getSoldQuantity()).isEqualTo(10);
            verify(inventoryRepository, never()).save(any());
        }
    }

    // =========================================================================
    // STOCK ADJUSTMENT
    // =========================================================================
    @Nested
    @DisplayName("Stock Adjustment")
    class StockAdjustmentTests {

        @Test
        @DisplayName("RESTOCK: should increase available quantity")
        void adjustStock_restock_increasesQuantity() {
            StockAdjustmentRequest request = StockAdjustmentRequest.builder()
                    .quantity(5).reason(StockAdjustmentReason.RESTOCK).build();

            when(inventoryRepository.findByProductIdWithLock(101L)).thenReturn(Optional.of(sampleInventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(sampleInventory);
            when(inventoryMapper.toResponse(any(Inventory.class))).thenReturn(sampleResponse);

            inventoryService.adjustStock(101L, request);

            assertThat(sampleInventory.getAvailableQuantity()).isEqualTo(15);
        }

        @Test
        @DisplayName("DAMAGE: should decrease available quantity")
        void adjustStock_damage_decreasesQuantity() {
            StockAdjustmentRequest request = StockAdjustmentRequest.builder()
                    .quantity(-2).reason(StockAdjustmentReason.DAMAGE).build();

            when(inventoryRepository.findByProductIdWithLock(101L)).thenReturn(Optional.of(sampleInventory));
            when(inventoryRepository.save(any(Inventory.class))).thenReturn(sampleInventory);
            when(inventoryMapper.toResponse(any(Inventory.class))).thenReturn(sampleResponse);

            inventoryService.adjustStock(101L, request);

            assertThat(sampleInventory.getAvailableQuantity()).isEqualTo(8);
        }

        @Test
        @DisplayName("Should throw when adjustment results in negative stock")
        void adjustStock_negativeResult_throws() {
            StockAdjustmentRequest request = StockAdjustmentRequest.builder()
                    .quantity(-20).reason(StockAdjustmentReason.CORRECTION).build();

            when(inventoryRepository.findByProductIdWithLock(101L)).thenReturn(Optional.of(sampleInventory));

            assertThatThrownBy(() -> inventoryService.adjustStock(101L, request))
                    .isInstanceOf(InvalidStockOperationException.class)
                    .hasMessageContaining("negative");

            // Stock unchanged
            assertThat(sampleInventory.getAvailableQuantity()).isEqualTo(10);
            verify(inventoryRepository, never()).save(any());
        }
    }

    // =========================================================================
    // LOW STOCK
    // =========================================================================
    @Nested
    @DisplayName("Low Stock Query")
    class LowStockTests {

        @Test
        @DisplayName("getLowStockInventory should call correct repository method")
        void getLowStockInventory_callsCorrectRepo() {
            org.springframework.data.domain.Page<Inventory> emptyPage =
                    org.springframework.data.domain.Page.empty();

            when(inventoryRepository.findLowStockItems(any())).thenReturn(emptyPage);

            inventoryService.getLowStockInventory(0, 20);

            verify(inventoryRepository).findLowStockItems(any());
        }
    }
}
