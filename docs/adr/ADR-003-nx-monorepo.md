# ADR-003: Nx como herramienta de monorepo

**Estado:** Aceptado  
**Fecha:** 2026-05-08

## Contexto

El proyecto tiene múltiples aplicaciones Angular (shell + 3 MFEs) y una librería compartida (`tsDesignSystem`). Necesitamos una herramienta que gestione las dependencias entre proyectos, optimice los builds y permita ejecutar tareas sólo en los proyectos afectados por un cambio.

## Opciones evaluadas

| Opción | Pros | Contras |
|---|---|---|
| Repositorios separados | Independencia total | Sin reutilización, CI complejo, versionado de dependencias compartidas |
| **Nx monorepo** | **Affected builds, task graph, generators, Module Federation nativo** | **Curva de aprendizaje inicial** |
| Turborepo | Rápido, agnóstico de framework | Soporte Angular/Module Federation menos maduro |
| Lerna | Histórico en JS | Mantenimiento limitado, no tiene task graph |

## Decisión

Usamos **Nx** con el plugin `@nx/angular` porque:

1. **Affected computation**: `nx affected --target=build` ejecuta builds sólo para proyectos impactados por el cambio — reduce tiempo de CI hasta 70% en PRs pequeños
2. **Module Federation nativo**: `@nx/angular:module-federation-dev-server` coordina el dev server del shell con todos los remotes automáticamente
3. **Generators**: creación de nuevos MFEs con la estructura correcta en un solo comando
4. **Task caching**: resultados cacheados local y remotamente (Nx Cloud opcional)

## Consecuencias

- **Positivo:** Un solo `pnpm install` para toda la capa frontend
- **Positivo:** `tsDesignSystem` se puede importar como librería local sin publicar a npm
- **Positivo:** El grafo de dependencias (`pnpm nx graph`) documenta las relaciones visualmente
- **Negativo:** El CI debe instalar pnpm y las dependencias de todo el monorepo incluso para cambios pequeños
