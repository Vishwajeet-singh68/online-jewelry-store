package com.jewelry.cart.mapper;

import com.jewelry.cart.dto.response.CartItemResponse;
import com.jewelry.cart.dto.response.CartResponse;
import com.jewelry.cart.entity.Cart;
import com.jewelry.cart.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CartMapper {

    CartItemResponse toCartItemResponse(CartItem cartItem);

    CartResponse toCartResponse(Cart cart);
}
