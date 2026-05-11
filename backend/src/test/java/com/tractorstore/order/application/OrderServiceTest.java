package com.tractorstore.order.application;

import com.tractorstore.order.application.dto.CreateOrderRequest;
import com.tractorstore.order.application.dto.CreateOrderRequest.OrderItemRequest;
import com.tractorstore.order.application.dto.OrderResponse;
import com.tractorstore.order.application.event.OrderPlaced;
import com.tractorstore.order.domain.model.Order;
import com.tractorstore.order.domain.model.Order.OrderStatus;
import com.tractorstore.order.domain.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService")
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private ApplicationEventPublisher eventPublisher;
    @InjectMocks private OrderService orderService;

    private CreateOrderRequest validRequest;
    private Order savedOrder;

    @BeforeEach
    void setUp() {
        validRequest = new CreateOrderRequest(
            "juan@test.com", "Juan Viteri",
            "Calle 123 #45-67, Cali, Valle",
            List.of(new OrderItemRequest(
                UUID.randomUUID(), "TractorPro X200",
                "Rojo", new BigDecimal("45000000"), 1
            ))
        );

        savedOrder = new Order();
        savedOrder.setOrderNumber("TS-12345-1001");
        savedOrder.setCustomerEmail("juan@test.com");
        savedOrder.setCustomerName("Juan Viteri");
        savedOrder.setTotal(new BigDecimal("45000000"));
        savedOrder.setItems(List.of());
    }

    @Nested
    @DisplayName("createOrder()")
    class CreateOrder {

        @Test
        @DisplayName("creates order with correct data")
        void createsOrderWithCorrectData() {
            when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

            OrderResponse response = orderService.createOrder(validRequest);

            assertThat(response.customerEmail()).isEqualTo("juan@test.com");
            assertThat(response.orderNumber()).isEqualTo("TS-12345-1001");
            verify(orderRepository).save(any(Order.class));
        }

        @Test
        @DisplayName("publishes OrderPlaced event after order creation")
        void publishesOrderPlacedEventAfterCreation() {
            when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

            orderService.createOrder(validRequest);

            ArgumentCaptor<OrderPlaced> eventCaptor = ArgumentCaptor.forClass(OrderPlaced.class);
            verify(eventPublisher).publishEvent(eventCaptor.capture());

            OrderPlaced event = eventCaptor.getValue();
            assertThat(event.customerEmail()).isEqualTo("juan@test.com");
        }

        @Test
        @DisplayName("calculates total from item prices")
        void calculatesTotalFromItemPrices() {
            CreateOrderRequest multiItemRequest = new CreateOrderRequest(
                "test@test.com", "Test User", "Test Address 123",
                List.of(
                    new OrderItemRequest(UUID.randomUUID(), "P1", "V1",
                        new BigDecimal("10000000"), 2),
                    new OrderItemRequest(UUID.randomUUID(), "P2", "V2",
                        new BigDecimal("5000000"), 1)
                )
            );

            Order orderWithItems = new Order();
            orderWithItems.setOrderNumber("TS-99999-1001");
            orderWithItems.setCustomerEmail("test@test.com");
            orderWithItems.setItems(List.of());
            orderWithItems.setTotal(new BigDecimal("25000000"));

            when(orderRepository.save(any())).thenReturn(orderWithItems);

            OrderResponse response = orderService.createOrder(multiItemRequest);

            assertThat(response.total()).isEqualByComparingTo("25000000");
        }
    }

    @Nested
    @DisplayName("confirmOrder()")
    class ConfirmOrder {

        @Test
        @DisplayName("transitions order from PENDING to CONFIRMED")
        void transitionsOrderToConfirmed() {
            UUID orderId = UUID.randomUUID();
            Order pendingOrder = new Order();
            pendingOrder.setOrderNumber("TS-11111-1001");
            pendingOrder.setCustomerEmail("test@test.com");
            pendingOrder.setItems(List.of());
            pendingOrder.setTotal(BigDecimal.ZERO);

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(pendingOrder));
            when(orderRepository.save(any())).thenReturn(pendingOrder);

            orderService.confirmOrder(orderId);

            assertThat(pendingOrder.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        }

        @Test
        @DisplayName("throws exception when order not found")
        void throwsExceptionWhenOrderNotFound() {
            UUID orderId = UUID.randomUUID();
            when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.confirmOrder(orderId))
                .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("cancelOrder()")
    class CancelOrder {

        @Test
        @DisplayName("cancels PENDING order successfully")
        void cancelsPendingOrderSuccessfully() {
            UUID orderId = UUID.randomUUID();
            Order pendingOrder = new Order();
            pendingOrder.setOrderNumber("TS-22222-1001");
            pendingOrder.setCustomerEmail("test@test.com");
            pendingOrder.setItems(List.of());
            pendingOrder.setTotal(BigDecimal.ZERO);

            when(orderRepository.findById(orderId)).thenReturn(Optional.of(pendingOrder));
            when(orderRepository.save(any())).thenReturn(pendingOrder);

            orderService.cancelOrder(orderId);

            assertThat(pendingOrder.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }
    }
}