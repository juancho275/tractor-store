# Architecture — The Tractor Store

## Visión general

Monolito modular en el backend (Spring Modulith) combinado con Micro Frontends en el frontend (Angular 19 + Module Federation), desplegado en Railway + Vercel.

```
┌─────────────────────────────────────────────────────────────┐
│                      FRONTEND (Vercel)                       │
│                                                             │
│  ┌──────────┐  ┌────────────┐  ┌───────────┐  ┌─────────┐  │
│  │  Shell   │  │ mfeExplore │  │ mfeDecide │  │mfeCheck-│  │
│  │ (host)   │◄─┤ (catálogo) │  │(producto) │  │  out    │  │
│  └────┬─────┘  └────────────┘  └───────────┘  └─────────┘  │
│       │              tsDesignSystem (compartido)             │
└───────┼─────────────────────────────────────────────────────┘
        │ HTTPS / REST
┌───────▼─────────────────────────────────────────────────────┐
│                   BACKEND (Railway)                          │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              Spring Boot 3.4.5 / Java 21             │   │
│  │                                                      │   │
│  │  ┌──────────┐ ┌───────────┐ ┌──────┐ ┌──────────┐   │   │
│  │  │ catalog  │ │ inventory │ │ cart │ │  order   │   │   │
│  │  └──────────┘ └───────────┘ └──────┘ └────┬─────┘   │   │
│  │                                            │ OrderPlaced│ │
│  │                                    ┌───────▼──────┐   │   │
│  │                                    │notifications │   │   │
│  │                                    └──────────────┘   │   │
│  │  ─────────── Spring Modulith event_publication ──────  │   │
│  └──────────────────────────────────────────────────────┘   │
│                          │                                   │
│                   PostgreSQL 17                              │
└─────────────────────────────────────────────────────────────┘
```

## Módulos del backend

| Módulo | Paquete | Responsabilidad |
|---|---|---|
| Catalog | `com.tractorstore.catalog` | Productos, variantes, categorías |
| Inventory | `com.tractorstore.inventory` | Stock, reservas, deducción |
| Cart | `com.tractorstore.cart` | Carrito de compras en sesión |
| Order | `com.tractorstore.order` | Checkout, ciclo de vida de órdenes |
| Notifications | `com.tractorstore.notifications` | Emails de confirmación vía Resend API |

La comunicación cross-módulo es **exclusivamente** vía Domain Events publicados con Spring Modulith. El test `ModularityTests.verifiesModularStructure()` verifica en CI que ningún módulo accede a las clases internas de otro.

## Flujo de checkout (evento de dominio)

```
POST /api/orders
     │
     ▼
OrderService.createOrder()
     │  (dentro de la misma transacción)
     ├─ Persiste Order + OrderItems
     └─ Publica OrderPlaced (outbox: event_publication table)
          │  (DESPUÉS del commit — Spring Modulith garantía)
          ├─ task-1: InventoryService → deduce stock
          └─ task-2: NotificationService → envía email HTML via Resend API
```

## ADRs (Architectural Decision Records)

- [ADR-001](docs/adr/ADR-001-spring-modulith.md) — Spring Modulith como arquitectura de Monolito Modular
- [ADR-002](docs/adr/ADR-002-module-federation.md) — Angular 19 + Module Federation para Micro Frontends
- [ADR-003](docs/adr/ADR-003-nx-monorepo.md) — Nx como herramienta de monorepo
- [ADR-004](docs/adr/ADR-004-outbox-pattern.md) — Patrón Outbox para Domain Events
- [ADR-005](docs/adr/ADR-005-storybook-chromatic-deferred.md) — Storybook/Chromatic diferido

## Stack técnico

| Capa | Tecnología |
|---|---|
| Backend | Java 21, Spring Boot 3.4.5, Spring Modulith 1.3.4 |
| Persistencia | PostgreSQL 17, Flyway, Spring Data JPA |
| Frontend | Angular 19, Nx, Module Federation (Webpack 5) |
| API docs | springdoc-openapi 2.8.5 (OpenAPI 3.1) |
| Seguridad | Spring Security, JJWT 0.12.6 |
| Observabilidad | Spring Actuator, Micrometer, Prometheus |
| Tests | JUnit 5, Mockito, JaCoCo (≥80%), Testcontainers |
| CI/CD | GitHub Actions, SonarCloud, OWASP Dependency Check |
| Deploy | Railway (backend), Vercel (frontend) |
