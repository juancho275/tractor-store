# Deployment — The Tractor Store

## Pipeline CI/CD

El pipeline corre en **GitHub Actions** (`.github/workflows/ci-backend.yml`) y se activa en push/PR a `main` y `develop`.

```
PR abierto
    │
    ├─ ✅ PR Checks          — valida título (Conventional Commits) y nombre de rama
    ├─ 🔧 Setup              — detecta qué cambió (backend / frontend)
    │
    ├─ 🧪 Backend Tests      — mvn test con PostgreSQL 17 efímero (JaCoCo ≥80%)
    ├─ 📊 SonarCloud         — análisis de calidad y cobertura (quality gate)
    ├─ 🏗️ Frontend Build     — pnpm nx build para todos los MFEs
    ├─ 🔒 OWASP Check        — CVE threshold 9.0 (bloquea en CVSS≥9)
    │
    └─ (solo en main)
         ├─ 💨 Smoke Tests   — health check en producción
         └─ ✅ Summary       — tabla de URLs en el resumen de GitHub

Deploy automático:
  - Railway: detecta push a main y redespliega el backend automáticamente
  - Vercel:  detecta push a main y redespliega cada MFE automáticamente
```

## Variables de entorno

### Backend (Railway)

| Variable | Descripción | Requerida |
|---|---|---|
| `PGHOST` | Host de PostgreSQL | Sí (auto por Railway) |
| `PGPORT` | Puerto de PostgreSQL | Sí (auto por Railway) |
| `PGDATABASE` | Nombre de la base de datos | Sí (auto por Railway) |
| `PGUSER` | Usuario de PostgreSQL | Sí (auto por Railway) |
| `PGPASSWORD` | Contraseña de PostgreSQL | Sí (auto por Railway) |
| `PORT` | Puerto del servidor (default: 8080) | No |
| `FRONTEND_URL` | URL del shell en Vercel (para CORS) | Sí |
| `RESEND_API_KEY` | API key de Resend para emails | No (sin emails si ausente) |
| `SPRING_MAIL_FROM` | Dirección remitente de emails | No (default: `onboarding@resend.dev`) |

### CI (GitHub Secrets)

| Secret | Descripción |
|---|---|
| `SONAR_TOKEN` | Token de SonarCloud para análisis de calidad |
| `GITHUB_TOKEN` | Auto-generado por GitHub Actions |

## Rollback manual (< 5 minutos)

### Backend (Railway)

1. Entrar a [Railway dashboard](https://railway.app)
2. Seleccionar el servicio `tractor-store-backend`
3. Ir a **Deployments** → buscar el último deployment exitoso
4. Click en **Redeploy** en ese deployment anterior
5. Verificar con smoke test:
   ```bash
   curl https://tractor-store-production.up.railway.app/actuator/health
   ```

### Frontend (Vercel)

1. Entrar a [Vercel dashboard](https://vercel.com)
2. Seleccionar el proyecto del MFE o shell
3. Ir a **Deployments** → click en los tres puntos del deployment anterior
4. Click **Promote to Production**
5. Verificar que la URL de producción responde

### Rollback via Git (alternativa)

```bash
# Revertir el último commit en main y forzar redespliegue
git revert HEAD --no-edit
git push origin main
```

## Smoke tests post-deploy

```bash
BASE=https://tractor-store-production.up.railway.app

# 1. Health check
curl --fail $BASE/actuator/health

# 2. Catálogo (debe retornar JSON con productos)
curl --fail $BASE/api/catalog/products

# 3. Categorías
curl --fail $BASE/api/catalog/categories

# 4. Frontend shell
curl --fail https://tractor-store-shell.vercel.app
```

Todos deben retornar HTTP 200. El pipeline los ejecuta automáticamente en cada push a `main`.

## Flyway — migraciones de base de datos

Las migraciones están en `backend/src/main/resources/db/migration/`. En producción, Flyway corre automáticamente al iniciar la aplicación con `spring.flyway.enabled=true`.

Para agregar una migración:
```
V8__descripcion_del_cambio.sql
```

El número debe ser mayor al último aplicado (visible en el log de Railway: `Current version of schema "public": 7`).
