package com.jewelry.order.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Order Service API",
        version = "1.0",
        description = "Order Microservice for Online Jewelry Store. Authentication and authorization are handled upstream at the API Gateway, which forwards user identity via X-User-Id header.",
        contact = @Contact(name = "Jewelry Store Engineering Team")
    )
)
public class OpenApiConfig {
}
