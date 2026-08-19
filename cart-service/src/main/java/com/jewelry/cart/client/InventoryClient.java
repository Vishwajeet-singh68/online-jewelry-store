package com.jewelry.cart.client;

import com.jewelry.cart.client.dto.InventoryClientDto;
import com.jewelry.cart.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventory-service", url = "${INVENTORY_SERVICE_URL:http://localhost:8083}", configuration = FeignConfig.class)
public interface InventoryClient {

    @GetMapping("/api/v1/inventory/{productId}")
    InventoryClientDto getInventoryByProductId(@PathVariable("productId") Long productId);
}
