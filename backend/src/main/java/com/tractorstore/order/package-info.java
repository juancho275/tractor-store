/**
 * Order bounded context — public module API.
 *
 * Exposes the event package so other modules can listen
 * to domain events without violating module boundaries.
 * Internal classes (repositories, entities) remain private.
 */
package com.tractorstore.order;