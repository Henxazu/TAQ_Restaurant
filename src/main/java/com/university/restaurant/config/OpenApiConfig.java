package com.university.restaurant.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * [M1] (Optional) Swagger UI for API testing -> http://localhost:8080/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI restaurantOpenApi() {
        return new OpenAPI().info(new Info()
                .title("TAQ Restaurant Management API")
                .version("v1")
                .description("REST API for Customer, Staff and Admin portals (/api/v1/**)"));
    }
}