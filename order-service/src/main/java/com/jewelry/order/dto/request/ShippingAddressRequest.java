package com.jewelry.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Shipping address request payload")
public class ShippingAddressRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 150, message = "Full name cannot exceed 150 characters")
    @Schema(description = "Full recipient name", example = "John Doe")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    @Schema(description = "Contact phone number", example = "9876543210")
    private String phoneNumber;

    @NotBlank(message = "Address line 1 is required")
    @Size(max = 255, message = "Address line 1 cannot exceed 255 characters")
    @Schema(description = "Primary street address", example = "123 Main Street")
    private String addressLine1;

    @Size(max = 255, message = "Address line 2 cannot exceed 255 characters")
    @Schema(description = "Secondary address line (apartment, suite, etc.)", example = "Apartment 4")
    private String addressLine2;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City cannot exceed 100 characters")
    @Schema(description = "City", example = "Mathura")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 100, message = "State cannot exceed 100 characters")
    @Schema(description = "State or region", example = "Uttar Pradesh")
    private String state;

    @NotBlank(message = "Postal code is required")
    @Size(max = 20, message = "Postal code cannot exceed 20 characters")
    @Schema(description = "Postal or ZIP code", example = "281001")
    private String postalCode;

    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country cannot exceed 100 characters")
    @Schema(description = "Country", example = "India")
    private String country;
}
