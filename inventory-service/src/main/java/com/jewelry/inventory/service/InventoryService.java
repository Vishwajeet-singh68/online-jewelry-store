package com.jewelry.inventory.service;

import com.jewelry.inventory.dto.request.CreateInventoryRequest;
import com.jewelry.inventory.dto.request.StockAdjustmentRequest;
import com.jewelry.inventory.dto.request.StockQuantityRequest;
import com.jewelry.inventory.dto.request.UpdateInventoryRequest;
import com.jewelry.inventory.dto.response.InventoryResponse;
import com.jewelry.inventory.dto.response.PageResponse;

public interface InventoryService {

    // ---- Admin operations ----
    InventoryResponse createInventory(CreateInventoryRequest request);
    InventoryResponse updateInventory(Long productId, UpdateInventoryRequest request);
    InventoryResponse adjustStock(Long productId, StockAdjustmentRequest request);
    PageResponse<InventoryResponse> getAllInventory(int page, int size);
    PageResponse<InventoryResponse> getLowStockInventory(int page, int size);

    // ---- Read operations (public & admin) ----
    InventoryResponse getInventoryByProductId(Long productId);

    // ---- Stock lifecycle operations (internal / order service) ----
    InventoryResponse reserveStock(Long productId, StockQuantityRequest request);
    InventoryResponse releaseStock(Long productId, StockQuantityRequest request);
    InventoryResponse deductStock(Long productId, StockQuantityRequest request);
}
