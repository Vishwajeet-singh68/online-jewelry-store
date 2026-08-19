package com.jewelry.order.config;

import com.jewelry.order.exception.InventoryReservationException;
import com.jewelry.order.exception.ServiceUnavailableException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return (String methodKey, Response response) -> {
            if (response.status() == 409) {
                return new InventoryReservationException("Unable to place order: stock reservation failed downstream");
            }
            if (response.status() == 404) {
                return new ServiceUnavailableException("Required item or resource not found in downstream service");
            }
            if (response.status() >= 500) {
                return new ServiceUnavailableException("Downstream microservice is temporarily unavailable");
            }
            return new ServiceUnavailableException("Error communicating with downstream microservice: HTTP " + response.status());
        };
    }
}
