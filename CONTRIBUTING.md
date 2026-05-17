# Contributing — The Tractor Store

## Flujo de Git (GitFlow)

```
main          ← producción (protegida, solo via PR desde release/*)
  └─ release/x.y.z ← estabilización antes de deploy
       └─ develop  ← integración continua (protegida, solo via PR)
            └─ feature/SCRUM-XX-descripcion  ← desarrollo
            └─ fix/SCRUM-XX-descripcion      ← correcciones
            └─ hotfix/SCRUM-XX-descripcion   ← urgentes desde main
```

### Reglas

- Nunca hacer push directo a `main` o `develop`
- El nombre de la rama debe seguir el patrón: `feature/SCRUM-XX-descripcion`
- Crear una rama por historia de usuario/ticket de Jira

## Conventional Commits

```
<tipo>(<scope opcional>): <descripción en presente>

feat(order): add order cancellation endpoint
fix(notifications): switch to Resend HTTP API
test(inventory): cover low-stock edge case
chore(ci): cache OWASP dependency database
docs(readme): add quick start guide
refactor(catalog): extract product mapper
ci(pipeline): add smoke tests on main
```

**Tipos válidos:** `feat` · `fix` · `test` · `docs` · `chore` · `refactor` · `ci` · `release`

Los títulos de PR también deben seguir este formato — el pipeline lo valida automáticamente.

## Cómo crear un PR

1. Crear rama desde `develop`:
   ```bash
   git checkout develop && git pull origin develop
   git checkout -b feature/SCRUM-XX-mi-feature
   ```
2. Desarrollar, hacer commits con Conventional Commits
3. Antes de abrir el PR, verificar localmente:
   ```bash
   # Backend
   cd backend && mvn verify
   # Frontend
   pnpm nx build shell
   ```
4. Abrir PR hacia `develop` en GitHub
5. Completar el checklist del PR template
6. Esperar que el pipeline CI pase (tests + coverage + SonarCloud)
7. Al menos 1 aprobación antes de mergear

## Checklist de PR

- [ ] Tests unitarios cubren el nuevo código
- [ ] `mvn verify` pasa localmente (incluye JaCoCo ≥80%)
- [ ] Sin warnings de compilación nuevos
- [ ] Swagger/OpenAPI actualizado si se añadieron/modificaron endpoints
- [ ] Variables de entorno nuevas documentadas en `DEPLOYMENT.md`
- [ ] ADR creado si se tomó una decisión arquitectónica significativa

## Correr tests localmente

```bash
# Backend — tests + coverage + quality gate
cd backend && mvn verify

# Backend — solo tests rápidos
cd backend && mvn test

# Frontend
pnpm nx test shell
pnpm nx test mfeExplore

# Verificar estructura modular Spring Modulith
cd backend && mvn test -Dtest=ModularityTests
```

## Convenciones de código

**Backend (Java)**
- Paquetes por módulo: `com.tractorstore.<modulo>.{domain,application,infrastructure}`
- DTOs como Java Records
- Inyección por constructor (no `@Autowired` en campos)
- Excepciones de negocio como `IllegalStateException` o `IllegalArgumentException`
- Respuestas de error en formato RFC 9457 (gestionadas por `GlobalExceptionHandler`)

**Frontend (Angular)**
- Signals para estado de componentes (`signal()`, `computed()`)
- Standalone components (sin NgModules)
- Componentes del design system desde `@tractor-store/ts-design-system`
