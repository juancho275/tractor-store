package com.tractorstore.cart.domain.repository;

import com.tractorstore.cart.domain.model.Cart;
import com.tractorstore.cart.domain.model.Cart.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findBySessionIdAndStatus(String sessionId, CartStatus status);
    List<Cart> findByStatusAndExpiresAtBefore(CartStatus status, LocalDateTime now);
}