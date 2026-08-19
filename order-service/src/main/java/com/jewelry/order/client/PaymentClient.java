package com.jewelry.order.client;

import com.jewelry.order.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "payment-service", url = "${PAYMENT_SERVICE_URL:http://localhost:8086}", configuration = FeignConfig.class)
public interface PaymentClient {

    @GetMapping("/api/v1/payment/health")
    String checkPaymentHealth();
}
