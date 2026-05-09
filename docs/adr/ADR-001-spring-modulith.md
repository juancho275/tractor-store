# ADR-001: Spring Modulith como arquitectura de Monolito Modular

**Estado:** Aceptado  
**Fecha:** 2026-05-08

## Decisión
Usamos Spring Modulith con módulos basados en paquetes, NO Maven Multi-Module.

## Diferencia clave
- Maven Multi-Module: cada módulo es un artefacto separado con su propio pom.xml
- Spring Modulith: un solo artefacto, módulos definidos por estructura de paquetes

## Módulos definidos
- `com.tractorstore.catalog` → Catálogo de productos
- `com.tractorstore.inventory` → Gestión de stock
- `com.tractorstore.cart` → Carrito de compras
- `com.tractorstore.order` → Ciclo de vida de órdenes
- `com.tractorstore.notifications` → Notificaciones por eventos

## Garantías técnicas
Spring Modulith verifica en tiempo de test que:
1. Ningún módulo accede a clases internas de otro módulo
2. La comunicación cross-módulo usa exclusivamente Domain Events
3. Los repositorios son privados a su módulo

## Evidencia
Test `ModularityTests.verifiesModularStructure()` pasa en CI/CD.