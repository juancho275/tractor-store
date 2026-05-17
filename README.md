# The Tractor Store

E-commerce platform for agricultural machinery — Hackathon CoE 2026.

[![CI/CD Pipeline](https://github.com/juancho275/tractor-store/actions/workflows/ci-backend.yml/badge.svg?branch=main)](https://github.com/juancho275/tractor-store/actions/workflows/ci-backend.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=juancho275_tractor-store&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=juancho275_tractor-store)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=juancho275_tractor-store&metric=coverage)](https://sonarcloud.io/summary/new_code?id=juancho275_tractor-store)

## Producción

| Componente | URL |
|---|---|
| Frontend (Shell) | https://tractor-store-shell.vercel.app |
| Backend API | https://tractor-store-production.up.railway.app |
| Swagger UI | https://tractor-store-production.up.railway.app/swagger-ui |
| SonarCloud | https://sonarcloud.io/project/overview?id=juancho275_tractor-store |

## Requisitos

| Herramienta | Versión mínima |
|---|---|
| Java | 21 (Temurin) |
| Maven | 3.9+ |
| Node.js | 22 LTS |
| pnpm | 10+ |
| Docker + Docker Compose | 24+ |

## Inicio rápido (local)

### 1. Levantar la base de datos

```bash
docker compose up -d
```

Esto inicia PostgreSQL 17 en `localhost:5432` (usuario `postgres`, password `flypassword`, DB `tractorstore`).

### 2. Backend

```bash
cd backend
mvn spring-boot:run
```

API disponible en http://localhost:8080 · Swagger en http://localhost:8080/swagger-ui

### 3. Frontend

```bash
pnpm install
pnpm nx serve shell
```

Shell disponible en http://localhost:4200

## Arquitectura

Monolito modular (backend) + Micro Frontends (frontend):

```
backend/
  com.tractorstore.catalog       → Catálogo y productos
  com.tractorstore.inventory     → Stock y reservas
  com.tractorstore.cart          → Carrito de compras
  com.tractorstore.order         → Ciclo de vida de órdenes
  com.tractorstore.notifications → Emails por eventos de dominio

apps/
  shell       → Host MFE (Angular + Module Federation)
  mfeExplore  → Exploración de catálogo
  mfeDecide   → Detalle de producto
  mfeCheckout → Carrito y checkout
packages/
  tsDesignSystem → Design System compartido
```

Ver [ARCHITECTURE.md](ARCHITECTURE.md) para diagramas y decisiones de diseño.

## Documentación técnica

- [ARCHITECTURE.md](ARCHITECTURE.md) — Diagrama de módulos y ADRs
- [CONTRIBUTING.md](CONTRIBUTING.md) — Flujo de Git, convenciones y checklist de PR
- [DEPLOYMENT.md](DEPLOYMENT.md) — CI/CD, variables de entorno y rollback
- [CHANGELOG.md](CHANGELOG.md) — Historial de cambios por versión

## Tests

```bash
# Backend (tests + cobertura JaCoCo)
cd backend && mvn verify

# Frontend
pnpm nx test shell
```

Cobertura mínima requerida: **80% instrucciones** (verificada en CI con JaCoCo).
