package com.jewelry.gateway.config;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Central configuration for public and admin route patterns.
 *
 * Public paths → No authentication required
 * Admin paths  → Authentication required + ROLE_ADMIN
 * All others   → Authentication required (any valid role)
 */
@Component
public class RouteConfig {

    /**
     * Paths accessible without a JWT token.
     */
    public static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh",
            // Public product browsing (read-only)
            "/api/v1/products",
            "/api/v1/products/",
            "/api/v1/categories",
            "/api/v1/categories/",
            // Actuator health
            "/actuator/health",
            // Swagger / OpenAPI docs per service
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-ui.html",
            "/webjars/"
    );

    /**
     * Path prefixes restricted to users with ROLE_ADMIN.
     */
    public static final List<String> ADMIN_PATHS = List.of(
            "/api/v1/admin/"
    );

    public boolean isPublic(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    public boolean isAdminOnly(String path) {
        return ADMIN_PATHS.stream().anyMatch(path::startsWith);
    }
}
