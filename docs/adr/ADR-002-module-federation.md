# ADR-002: Angular 19 + Module Federation para Micro Frontends

**Estado:** Aceptado  
**Fecha:** 2026-05-08

## Contexto

El frontend necesita que distintos equipos puedan desarrollar y desplegar secciones del e-commerce de forma independiente (explorar catálogo, ver detalle de producto, completar checkout) sin que un cambio en un área requiera redesplegar toda la aplicación.

## Opciones evaluadas

| Opción | Pros | Contras |
|---|---|---|
| SPA monolítica Angular | Simple, un solo bundle | Acoplamiento total, despliegue conjunto obligatorio |
| **Module Federation (Webpack 5)** | **Despliegue independiente por MFE, lazy loading real** | **Complejidad de configuración inicial** |
| iframes | Aislamiento total | UX degradada, compartir estado es difícil |
| Web Components | Estándar web | Ecosistema Angular menos maduro para este patrón |

## Decisión

Usamos **Angular 19 con Module Federation** gestionado por **Nx** (plugin `@nx/angular`):

- `shell` — Host application, carga los remotes en tiempo de ejecución
- `mfeExplore` — Exploración de catálogo de productos
- `mfeDecide` — Detalle de producto y selección de variante
- `mfeCheckout` — Carrito y proceso de compra
- `tsDesignSystem` — Librería de componentes compartida (no es un MFE remoto)

Cada MFE se despliega en Vercel de forma independiente. El shell resuelve las URLs de los remotes en runtime mediante variables de entorno, lo que permite actualizar un MFE sin tocar los demás.

## Consecuencias

- **Positivo:** Autonomía de despliegue por módulo de negocio
- **Positivo:** `tsDesignSystem` garantiza consistencia visual sin duplicar código
- **Negativo:** La versión compartida de Angular debe mantenerse alineada entre todos los remotes (singleton en Module Federation)
- **Negativo:** El debugging cross-MFE requiere source maps coordinados
