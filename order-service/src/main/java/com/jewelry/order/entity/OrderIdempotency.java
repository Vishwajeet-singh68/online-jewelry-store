package com.jewelry.order.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "order_idempotency",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_idempotency_user_key", columnNames = {"user_id", "idempotency_key"})
    },
    indexes = {
        @Index(name = "idx_idempotency_user_key", columnList = "user_id, idempotency_key")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderIdempotency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "idempotency_key", nullable = false, length = 100)
    private String idempotencyKey;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
