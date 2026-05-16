package com.tractorstore.shared.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tractorStoreOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Tractor Store API")
                .description("""
                    REST API for **The Tractor Store** — agricultural machinery e-commerce platform.

                    Built with **Spring Modulith** (modular monolith) composed of four bounded contexts:
                    **Catalog**, **Inventory**, **Cart**, and **Orders**.
                    Domain events connect modules without direct coupling (e.g., OrderPlaced triggers inventory deduction).

                    Errors follow **RFC 9457** (Problem Details for HTTP APIs) — every error response
                    includes `type`, `title`, `status`, and `detail` fields.
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("Tractor Store — CoE Hackathon")
                    .email("juan.viteri9602@gmail.com"))
                .license(new License().name("MIT")))
            .servers(List.of(
                new Server()
                    .url("https://tractor-store-production.up.railway.app")
                    .description("Production (Railway)"),
                new Server()
                    .url("http://localhost:8080")
                    .description("Local development")
            ));
    }
}
