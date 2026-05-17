# Changelog

Formato basado en [Keep a Changelog](https://keepachangelog.com/es/1.1.0/).

## [1.0.0] — 2026-05-16

### Añadido

- **Módulo Catalog**: API REST para productos, variantes y categorías (`/api/catalog/products`, `/api/catalog/categories`)
- **Módulo Inventory**: Gestión de stock con reservas y deducción atómica (`/api/inventory/stock`)
- **Módulo Cart**: Carrito de compras por sesión (`/api/cart`)
- **Módulo Order**: Checkout completo con ciclo de vida PENDING → CONFIRMED → CANCELLED (`/api/orders`)
- **Módulo Notifications**: Email HTML de confirmación de orden vía Resend API (post-commit, asíncrono)
- **OpenAPI / Swagger UI**: Documentación interactiva en `/swagger-ui` con springdoc-openapi 2.8.5
- **GlobalExceptionHandler**: Respuestas de error RFC 9457 (ProblemDetail) para todos los módulos
- **Spring Modulith**: Verificación de estructura modular en CI (`ModularityTests`)
- **Domain Event Outbox**: `OrderPlaced` publicado post-commit con entrega garantizada a Inventory y Notifications
- **CI/CD Pipeline**: GitHub Actions con 8 etapas (PR validation, tests, SonarCloud, OWASP, smoke tests)
- **JaCoCo**: Quality gate de cobertura ≥80% de instrucciones
- **Deploy**: Backend en Railway, Frontend en Vercel con despliegue automático desde `main`
- **Flyway**: 7 migraciones de base de datos aplicadas automáticamente en Railway (PostgreSQL 17)
- **Micro Frontends**: Shell + mfeExplore + mfeDecide + mfeCheckout con Angular Module Federation
- **Design System**: `tsDesignSystem` como librería compartida en el monorepo Nx

### Infraestructura

- PostgreSQL 17 provisionado automáticamente por Railway
- Variables de entorno gestionadas por Railway (no hay credenciales en el código)
- SonarCloud configurado con exclusiones para DTOs, modelos de dominio y configuración
