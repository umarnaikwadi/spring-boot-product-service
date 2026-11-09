package com.ecommerce.product.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI productServiceAPI(){
        return new OpenAPI().info(new Info().title("E-Commerce Product Service API").
                description("Rest APIs for product management, "+ "including Crud operation, searching, " +
                        "Filtering, pagination and sorting.").version("1.0.0"));
    }
}
