package com.jewelry.order.client;

import com.jewelry.order.client.dto.CartClientDto;
import com.jewelry.order.client.dto.CartValidationClientDto;
import com.jewelry.order.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "cart-service", url = "${CART_SERVICE_URL:http://localhost:8084}", configuration = FeignConfig.class)
public interface CartClient {

    @GetMapping("/api/v1/cart")
    CartClientDto getCart(@RequestHeader("X-User-Id") Long userId);

    @GetMapping("/api/v1/cart/validate")
    CartValidationClientDto validateCart(@RequestHeader("X-User-Id") Long userId);

    @DeleteMapping("/api/v1/cart")
    CartClientDto clearCart(@RequestHeader("X-User-Id") Long userId);
}
