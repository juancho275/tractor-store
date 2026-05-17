# ADR-004: Patrón Outbox para Domain Events (Spring Modulith)

**Estado:** Aceptado  
**Fecha:** 2026-05-16

## Contexto

Al crear una orden necesitamos notificar a otros módulos (Inventory, Notifications) de que ocurrió el evento `OrderPlaced`. El reto es garantizar que si la transacción de la orden falla, los eventos no se publican; y si la transacción tiene éxito, los eventos se entregan aunque el sistema falle justo después del commit.

## Opciones evaluadas

| Opción | Garantía de entrega | Complejidad |
|---|---|---|
| Publicar evento dentro de la transacción (`ApplicationEventPublisher`) | Ninguna — si el handler falla, la transacción puede revertirse | Baja |
| Mensajería externa (Kafka, RabbitMQ) | Alta | Alta — infraestructura adicional |
| **Outbox via Spring Modulith** | **Exactamente-una-vez dentro del mismo proceso** | **Baja — integrada en el framework** |

## Decisión

Usamos el **mecanismo de publicación de eventos de Spring Modulith**, que implementa el patrón Outbox de forma transparente:

1. `OrderService.createOrder()` publica `OrderPlaced` dentro de la transacción usando `ApplicationEventPublisher`
2. Spring Modulith persiste el evento en la tabla `event_publication` en la misma transacción atómica
3. **Después del commit**, Spring Modulith entrega el evento a los listeners:
   - `InventoryService.on(OrderPlaced)` — en `task-1`
   - `OrderNotificationListener.on(OrderPlaced)` — en `task-2`
4. Ambos listeners se anotan con `@ApplicationModuleListener` que garantiza ejecución post-commit y manejo de reintentos

## Consecuencias

- **Positivo:** Consistencia atómica garantizada — el evento solo existe si la orden fue persistida
- **Positivo:** Reintentos automáticos si un listener falla
- **Positivo:** Sin infraestructura de mensajería externa — PostgreSQL actúa como broker
- **Positivo:** La tabla `event_publication` sirve como audit log de eventos de dominio
- **Negativo:** Los listeners corren en el mismo proceso — no escala a múltiples instancias sin coordinación (aceptable para monolito modular)
- **Negativo:** Si se necesita comunicación entre servicios separados en el futuro, habría que migrar a un broker externo
