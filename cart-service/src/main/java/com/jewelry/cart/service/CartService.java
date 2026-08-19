package com.jewelry.cart.service;

import com.jewelry.cart.dto.request.AddCartItemRequest;
import com.jewelry.cart.dto.request.UpdateCartItemRequest;
import com.jewelry.cart.dto.response.CartResponse;
import com.jewelry.cart.dto.response.CartValidationResponse;

public interface CartService {

    CartResponse getCartByUserId(Long userId);

    CartResponse addItemToCart(Long userId, AddCartItemRequest request);

    CartResponse updateCartItemQuantity(Long userId, Long itemId, UpdateCartItemRequest request);

    CartResponse removeItemFromCart(Long userId, Long itemId);

    CartResponse clearCart(Long userId);

    CartValidationResponse validateCart(Long userId);
}
