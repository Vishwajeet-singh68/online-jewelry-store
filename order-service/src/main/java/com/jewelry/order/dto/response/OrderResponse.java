package com.jewelry.order.dto.response;

import com.jewelry.order.enums.OrderStatus;
import com.jewelry.order.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Order details response payload")
public class OrderResponse {

    @Schema(description = "Order ID", example = "1")
    private Long id;

    @Schema(description = "Human-friendly unique order number", example = "ORD-20260819-000001")
    private String orderNumber;

    @Schema(description = "User ID", example = "101")
    private Long userId;

    @Schema(description = "Current order status", example = "CONFIRMED")
    private OrderStatus status;

    @Schema(description = "Current payment status", example = "PENDING")
    private PaymentStatus paymentStatus;

    @Schema(description = "Purchased items list")
    private List<OrderItemResponse> items;

    @Schema(description = "Total order amount", example = "100000.00")
    private BigDecimal totalAmount;

    @Schema(description = "Shipping address snapshot")
    private ShippingAddressResponse shippingAddress;

    @Schema(description = "Order creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last updated timestamp")
    private LocalDateTime updatedAt;
}
