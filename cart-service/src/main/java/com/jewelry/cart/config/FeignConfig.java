package com.jewelry.cart.config;

import com.jewelry.cart.exception.ProductUnavailableException;
import com.jewelry.cart.exception.ServiceUnavailableException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return (String methodKey, Response response) -> {
            if (response.status() == 404) {
                return new ProductUnavailableException("Product or inventory record not found in downstream service");
            }
            if (response.status() >= 500) {
                return new ServiceUnavailableException("Downstream microservice is temporarily unavailable");
            }
            return new ServiceUnavailableException("Error communicating with downstream microservice: HTTP " + response.status());
        };
    }
}
