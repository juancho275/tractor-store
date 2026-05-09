package com.tractorstore.order.domain.repository;

import com.tractorstore.order.domain.model.Order;
import com.tractorstore.order.domain.model.Order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByCustomerEmail(String email);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}