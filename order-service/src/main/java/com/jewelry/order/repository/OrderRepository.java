package com.jewelry.order.repository;

import com.jewelry.order.entity.Order;
import com.jewelry.order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Optional<Order> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id AND o.userId = :userId")
    Optional<Order> findByIdAndUserIdWithItems(@Param("id") Long id, @Param("userId") Long userId);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    Page<Order> findByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}
