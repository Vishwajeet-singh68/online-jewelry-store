package com.jewelry.order.dto.request;

import com.jewelry.order.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Admin order status update request payload")
public class UpdateOrderStatusRequest {

    @NotNull(message = "Order status is required")
    @Schema(description = "Target status transition", example = "PROCESSING")
    private OrderStatus status;
}
