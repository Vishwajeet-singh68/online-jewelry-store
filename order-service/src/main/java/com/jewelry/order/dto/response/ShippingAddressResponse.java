package com.jewelry.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Shipping address response payload")
public class ShippingAddressResponse {

    @Schema(description = "Full recipient name", example = "John Doe")
    private String fullName;

    @Schema(description = "Contact phone number", example = "9876543210")
    private String phoneNumber;

    @Schema(description = "Primary street address", example = "123 Main Street")
    private String addressLine1;

    @Schema(description = "Secondary address line", example = "Apartment 4")
    private String addressLine2;

    @Schema(description = "City", example = "Mathura")
    private String city;

    @Schema(description = "State or region", example = "Uttar Pradesh")
    private String state;

    @Schema(description = "Postal or ZIP code", example = "281001")
    private String postalCode;

    @Schema(description = "Country", example = "India")
    private String country;
}
