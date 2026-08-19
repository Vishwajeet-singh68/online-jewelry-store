package com.jewelry.cart.repository;

import com.jewelry.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserId(Long userId);

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.userId = :userId")
    Optional<Cart> findByUserIdWithItems(@Param("userId") Long userId);

    boolean existsByUserId(Long userId);
}
