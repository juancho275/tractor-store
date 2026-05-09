/**
 * Named interface for Order domain events.
 * Explicitly exposes this package so other modules
 * can listen to events without violating boundaries.
 */
@org.springframework.modulith.NamedInterface("events")
package com.tractorstore.order.application.event;