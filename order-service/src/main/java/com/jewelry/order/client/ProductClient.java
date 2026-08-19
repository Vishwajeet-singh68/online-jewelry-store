package com.jewelry.order.client;

import com.jewelry.order.client.dto.ProductClientDto;
import com.jewelry.order.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "${PRODUCT_SERVICE_URL:http://localhost:8082}", configuration = FeignConfig.class)
public interface ProductClient {

    @GetMapping("/api/v1/products/{id}")
    ProductClientDto getProductById(@PathVariable("id") Long id);
}
