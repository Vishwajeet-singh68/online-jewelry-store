package com.jewelry.order.client;

import com.jewelry.order.client.dto.InventoryClientDto;
import com.jewelry.order.client.dto.ReserveStockRequest;
import com.jewelry.order.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service", url = "${INVENTORY_SERVICE_URL:http://localhost:8083}", configuration = FeignConfig.class)
public interface InventoryClient {

    @GetMapping("/api/v1/inventory/{productId}")
    InventoryClientDto getInventoryByProductId(@PathVariable("productId") Long productId);

    @PostMapping("/api/v1/inventory/reserve")
    InventoryClientDto reserveStock(@RequestBody ReserveStockRequest request);

    @PostMapping("/api/v1/inventory/release")
    InventoryClientDto releaseStock(@RequestBody ReserveStockRequest request);
}
