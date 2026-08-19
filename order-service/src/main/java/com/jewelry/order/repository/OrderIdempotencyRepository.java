package com.jewelry.order.repository;

import com.jewelry.order.entity.OrderIdempotency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderIdempotencyRepository extends JpaRepository<OrderIdempotency, Long> {

    Optional<OrderIdempotency> findByUserIdAndIdempotencyKey(Long userId, String idempotencyKey);
}
