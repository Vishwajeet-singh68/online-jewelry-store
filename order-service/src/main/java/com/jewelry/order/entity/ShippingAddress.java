package com.jewelry.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingAddress {

    @Column(name = "shipping_full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "shipping_phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "shipping_address_line1", nullable = false, length = 255)
    private String addressLine1;

    @Column(name = "shipping_address_line2", length = 255)
    private String addressLine2;

    @Column(name = "shipping_city", nullable = false, length = 100)
    private String city;

    @Column(name = "shipping_state", nullable = false, length = 100)
    private String state;

    @Column(name = "shipping_postal_code", nullable = false, length = 20)
    private String postalCode;

    @Column(name = "shipping_country", nullable = false, length = 100)
    private String country;
}
