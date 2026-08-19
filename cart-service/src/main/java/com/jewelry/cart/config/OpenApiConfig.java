package com.jewelry.cart.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Cart Service API",
        version = "1.0",
        description = "Cart Management Microservice for Online Jewelry Store (Gateway Architecture)",
        contact = @Contact(name = "Jewelry Store Engineering Team")
    )
)
public class OpenApiConfig {
}
