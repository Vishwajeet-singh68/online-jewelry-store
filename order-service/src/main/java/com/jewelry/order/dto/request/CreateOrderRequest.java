package com.jewelry.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload to place a new order from user's current cart")
public class CreateOrderRequest {

    @NotNull(message = "Shipping address is required")
    @Valid
    @Schema(description = "Shipping address details")
    private ShippingAddressRequest shippingAddress;
}
