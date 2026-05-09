package com.tractorstore.order.infrastructure.web;

import com.tractorstore.order.application.OrderService;
import com.tractorstore.order.application.dto.CreateOrderRequest;
import com.tractorstore.order.application.dto.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for the Order module.
 * Base path: /api/orders
 */
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order lifecycle management API")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Create a new order (checkout)")
    public ResponseEntity<OrderResponse> createOrder(
        @Valid @RequestBody CreateOrderRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(orderService.createOrder(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order by order number")
    public ResponseEntity<OrderResponse> getOrderByNumber(
        @PathVariable String orderNumber
    ) {
        return ResponseEntity.ok(orderService.getOrderByNumber(orderNumber));
    }

    @GetMapping("/customer/{email}")
    @Operation(summary = "Get orders by customer email")
    public ResponseEntity<List<OrderResponse>> getOrdersByEmail(
        @PathVariable String email
    ) {
        return ResponseEntity.ok(orderService.getOrdersByEmail(email));
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "Confirm an order")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.confirmOrder(id));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel an order")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }
}