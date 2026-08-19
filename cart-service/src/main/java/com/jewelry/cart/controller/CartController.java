package com.jewelry.cart.controller;

import com.jewelry.cart.dto.request.AddCartItemRequest;
import com.jewelry.cart.dto.request.UpdateCartItemRequest;
import com.jewelry.cart.dto.response.CartResponse;
import com.jewelry.cart.dto.response.CartValidationResponse;
import com.jewelry.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Cart Management APIs (User context forwarded by API Gateway via X-User-Id header)")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get user cart", description = "Retrieves cart for user ID passed in X-User-Id header by API Gateway.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cart retrieved successfully")
    })
    public ResponseEntity<CartResponse> getCart(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart", description = "Validates product status and inventory before adding item. Combines quantity if item already in cart.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item added to cart"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "409", description = "Insufficient stock or product unavailable"),
        @ApiResponse(responseCode = "503", description = "Downstream service unavailable")
    })
    public ResponseEntity<CartResponse> addItem(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
            @Valid @RequestBody AddCartItemRequest request) {
        return ResponseEntity.ok(cartService.addItemToCart(userId, request));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update cart item quantity", description = "Updates quantity for a specific line item in user cart.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cart item updated"),
        @ApiResponse(responseCode = "404", description = "Cart item not found"),
        @ApiResponse(responseCode = "409", description = "Insufficient stock")
    })
    public ResponseEntity<CartResponse> updateItem(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateCartItemQuantity(userId, itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from cart", description = "Deletes a line item from user cart and recalculates total.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item removed successfully"),
        @ApiResponse(responseCode = "404", description = "Cart item not found")
    })
    public ResponseEntity<CartResponse> removeItem(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeItemFromCart(userId, itemId));
    }

    @DeleteMapping
    @Operation(summary = "Clear user cart", description = "Removes all items from user cart.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cart cleared successfully")
    })
    public ResponseEntity<CartResponse> clearCart(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        return ResponseEntity.ok(cartService.clearCart(userId));
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate cart pre-checkout", description = "Checks product active status, price changes, and current stock for all items in user cart.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cart validation completed")
    })
    public ResponseEntity<CartValidationResponse> validateCart(
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        return ResponseEntity.ok(cartService.validateCart(userId));
    }
}
