package com.jewelry.inventory.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Inventory Service API",
                version = "1.0",
                description = "Jewelry Store Inventory Service — manages product stock, reservations, releases, deductions, and low-stock monitoring. Authentication is handled by the API Gateway.",
                contact = @Contact(name = "Jewelry Store Team")
        )
)
public class OpenApiConfig {
}
