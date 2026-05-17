package com.tractorstore.order.application;

import com.tractorstore.order.application.dto.CreateOrderRequest;
import com.tractorstore.order.application.dto.OrderResponse;
import com.tractorstore.order.application.event.OrderPlaced;
import com.tractorstore.order.domain.model.Order;
import com.tractorstore.order.domain.model.OrderItem;
import com.tractorstore.order.domain.repository.OrderRepository;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Application service for the Order bounded context.
 *
 * <p>Manages the complete order lifecycle and publishes domain events
 * via Spring Modulith's Outbox pattern.
 *
 * <p>Key architectural decision: the OrderPlaced event is published
 * within the same transaction that creates the order. Spring Modulith
 * persists the event in the event_publication table and delivers it
 * AFTER the transaction commits, guaranteeing consistency.
 *
 * <p>This means if the order creation fails (e.g., database error),
 * the event is NOT published — no stock deduction, no email sent.
 */
@Service
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MeterRegistry meterRegistry;
    private final AtomicLong orderSequence = new AtomicLong(1000);

    public OrderService(OrderRepository orderRepository,
                        ApplicationEventPublisher eventPublisher,
                        MeterRegistry meterRegistry) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.meterRegistry = meterRegistry;
    }

    /**
     * Creates a new order from checkout data and publishes OrderPlaced event.
     *
     * <p>The event is published within this transaction. Spring Modulith
     * stores it in event_publication table and delivers AFTER COMMIT.
     *
     * @param request checkout data with customer info and items
     * @return created order response
     */
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomerEmail(request.customerEmail());
        order.setCustomerName(request.customerName());
        order.setShippingAddress(request.shippingAddress());

        List<OrderItem> items = request.items().stream()
            .map(i -> {
                OrderItem item = new OrderItem();
                item.setProductVariantId(i.productVariantId());
                item.setProductName(i.productName());
                item.setVariantName(i.variantName());
                item.setUnitPrice(i.unitPrice());
                item.setQuantity(i.quantity());
                return item;
            })
            .toList();

        order.setItems(items);
        order.setTotal(
            items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
        );

        Order saved = orderRepository.save(order);

        meterRegistry.counter("orders.placed",
            "app", "tractor-store-backend"
        ).increment();

        // Publish domain event — Spring Modulith stores in event_publication
        // and delivers AFTER this transaction commits (Outbox pattern)
        eventPublisher.publishEvent(new OrderPlaced(
            saved.getId(),
            saved.getOrderNumber(),
            saved.getCustomerEmail(),
            saved.getTotal(),
            items.stream()
                .map(i -> new OrderPlaced.OrderPlacedItem(
                    i.getProductVariantId(), i.getProductName(),
                    i.getVariantName(), i.getUnitPrice(), i.getQuantity()
                ))
                .toList()
        ));

        return toResponse(saved);
    }

    public OrderResponse getOrder(UUID id) {
        return orderRepository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
    }

    public OrderResponse getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
            .map(this::toResponse)
            .orElseThrow(() -> new EntityNotFoundException(
                "Order not found: " + orderNumber));
    }

    public List<OrderResponse> getOrdersByEmail(String email) {
        return orderRepository.findByCustomerEmail(email)
            .stream().map(this::toResponse).toList();
    }

    @Transactional
    public OrderResponse confirmOrder(UUID id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
        order.confirm();
        return toResponse(orderRepository.save(order));
    }

    @Transactional
    public OrderResponse cancelOrder(UUID id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Order not found: " + id));
        order.cancel();
        return toResponse(orderRepository.save(order));
    }

    private String generateOrderNumber() {
        return "TS-" + System.currentTimeMillis() % 100000 +
               "-" + orderSequence.incrementAndGet();
    }

    private OrderResponse toResponse(Order o) {
        var items = o.getItems().stream()
            .map(i -> new OrderResponse.OrderItemResponse(
                i.getProductVariantId(), i.getProductName(),
                i.getVariantName(), i.getUnitPrice(),
                i.getQuantity(), i.getSubtotal()
            ))
            .toList();

        return new OrderResponse(
            o.getId(), o.getOrderNumber(), o.getCustomerEmail(),
            o.getCustomerName(), items, o.getTotal(),
            o.getStatus().name(), o.getShippingAddress(), o.getCreatedAt()
        );
    }
}