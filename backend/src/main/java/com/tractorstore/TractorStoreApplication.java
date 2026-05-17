package com.tractorstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point of The Tractor Store Backend.
 *
 * <p>Modular Monolith architecture using Spring Modulith.
 * Each business domain is isolated in its own package:
 * <ul>
 *   <li>{@code catalog}     - Product catalog and categories</li>
 *   <li>{@code inventory}   - Stock management per SKU</li>
 *   <li>{@code cart}        - Shopping cart sessions</li>
 *   <li>{@code order}       - Order lifecycle management</li>
 *   <li>{@code notifications} - Event-driven notifications</li>
 *   <li>{@code payment}       - Payment simulation (PENDING → CONFIRMED via Outbox)</li>
 * </ul>
 *
 * <p>Modules communicate exclusively via Domain Events (Spring Modulith).
 * Direct cross-module dependencies are forbidden and enforced at test time.
 *
 * @see <a href="http://localhost:8080/swagger-ui">Swagger UI</a>
 * @see <a href="http://localhost:8080/actuator/health">Health Check</a>
 */
@SpringBootApplication
public class TractorStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(TractorStoreApplication.class, args);
    }
}