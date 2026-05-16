/**
 * Notifications module — 5th bounded context in the Tractor Store modular monolith.
 *
 * <p>Listens to domain events from other modules (Order) and sends
 * transactional notifications to customers. Uses Spring Modulith's
 * @ApplicationModuleListener to receive events AFTER order transaction commits.
 *
 * <p>No direct dependency on Order internals — only the published event contract
 * (OrderPlaced) is consumed, following the Spring Modulith architecture.
 */
@org.springframework.modulith.ApplicationModule(displayName = "Notifications")
package com.tractorstore.notifications;
