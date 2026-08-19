package com.jewelry.product.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Product Service API",
                version = "1.0",
                description = "Jewelry Store Product Service microservice providing catalog management, search, filtering, and admin operations. (Security handled at API Gateway)",
                contact = @Contact(name = "Jewelry Store Team")
        )
)
public class OpenApiConfig {
}
