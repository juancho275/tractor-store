/**
 * Payment module — simulates payment gateway processing.
 *
 * <p>Listens to the OrderPlaced domain event published by the Order module
 * and confirms the order after a simulated processing delay.
 * Communicates exclusively via Spring Modulith domain events.
 */
@org.springframework.modulith.ApplicationModule(
    displayName = "Payment"
)
package com.tractorstore.payment;
