# ADR-005: Storybook + Chromatic diferidos fuera del alcance inicial

**Estado:** Diferido  
**Fecha:** 2026-05-08

## Contexto

El design system (`tsDesignSystem`) contiene componentes Angular compartidos entre los MFEs. Para documentar, aislar y hacer pruebas visuales de esos componentes existe Storybook (catálogo interactivo) y Chromatic (regresión visual automatizada en CI).

## Decisión

**Se difiere la integración de Storybook y Chromatic** para después de la entrega del hackathon (deadline 2026-05-24).

## Razones

1. **Tiempo**: la configuración de Storybook con Angular 19 standalone components y Module Federation requiere ajustes no triviales; el ROI no justifica el esfuerzo en 8 días de sprint
2. **Prioridad**: las historias de valor de negocio (módulos de backend, notificaciones, documentación técnica) tienen mayor peso en la evaluación
3. **Alternativa suficiente**: los componentes del design system se validan visualmente durante el desarrollo normal de los MFEs y se cubren con tests unitarios de Angular

## Consecuencias

- **Positivo:** Se libera tiempo para features de mayor impacto en la evaluación
- **Negativo:** No hay catálogo de componentes navegable para nuevos desarrolladores
- **Negativo:** Los cambios visuales en `tsDesignSystem` no tienen regresión visual automatizada
- **Plan de retoma:** Integrar Storybook 8 + Chromatic en el primer sprint post-entrega si el proyecto continúa
