/**
 * Inventory bounded context.
 * Allowed to depend on Order's events named interface
 * for listening to OrderPlaced domain events.
 */
@org.springframework.modulith.ApplicationModule(
    allowedDependencies = "order :: events"
)
package com.tractorstore.inventory;