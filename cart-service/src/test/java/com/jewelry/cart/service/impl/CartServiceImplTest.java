package com.jewelry.cart.service.impl;

import com.jewelry.cart.client.InventoryClient;
import com.jewelry.cart.client.ProductClient;
import com.jewelry.cart.client.dto.InventoryClientDto;
import com.jewelry.cart.client.dto.ProductClientDto;
import com.jewelry.cart.dto.request.AddCartItemRequest;
import com.jewelry.cart.dto.request.UpdateCartItemRequest;
import com.jewelry.cart.dto.response.CartResponse;
import com.jewelry.cart.dto.response.CartValidationResponse;
import com.jewelry.cart.entity.Cart;
import com.jewelry.cart.entity.CartItem;
import com.jewelry.cart.exception.InsufficientStockException;
import com.jewelry.cart.exception.ProductUnavailableException;
import com.jewelry.cart.mapper.CartMapper;
import com.jewelry.cart.repository.CartItemRepository;
import com.jewelry.cart.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductClient productClient;

    @Mock
    private InventoryClient inventoryClient;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private Long userId = 101L;
    private Cart cart;
    private ProductClientDto activeProduct;
    private InventoryClientDto availableInventory;

    @BeforeEach
    void setUp() {
        cart = Cart.builder()
                .id(1L)
                .userId(userId)
                .totalAmount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .build();

        activeProduct = ProductClientDto.builder()
                .id(101L)
                .name("Diamond Gold Ring")
                .sku("JW-RING-18K-001")
                .imageUrl("https://cdn.jewelry.com/ring.jpg")
                .price(new BigDecimal("50000.00"))
                .status("ACTIVE")
                .build();

        availableInventory = InventoryClientDto.builder()
                .id(1L)
                .productId(101L)
                .sku("JW-RING-18K-001")
                .availableQuantity(10)
                .reservedQuantity(0)
                .status("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("getCart - should create new cart if none exists")
    void getCartByUserId_CreatesNewCart() {
        when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.toCartResponse(any(Cart.class))).thenReturn(new CartResponse());

        CartResponse response = cartService.getCartByUserId(userId);

        assertNotNull(response);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    @DisplayName("addItemToCart - should successfully add new item when stock is available")
    void addItemToCart_SuccessNewItem() {
        AddCartItemRequest request = AddCartItemRequest.builder()
                .productId(101L)
                .quantity(2)
                .build();

        when(productClient.getProductById(101L)).thenReturn(activeProduct);
        when(inventoryClient.getInventoryByProductId(101L)).thenReturn(availableInventory);
        when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cartMapper.toCartResponse(any(Cart.class))).thenAnswer(invocation -> {
            Cart saved = invocation.getArgument(0);
            return CartResponse.builder()
                    .id(saved.getId())
                    .userId(saved.getUserId())
                    .totalAmount(saved.getTotalAmount())
                    .build();
        });

        CartResponse response = cartService.addItemToCart(userId, request);

        assertNotNull(response);
        assertEquals(new BigDecimal("100000.00"), response.getTotalAmount());
        assertEquals(1, cart.getItems().size());
    }

    @Test
    @DisplayName("addItemToCart - should accumulate quantity when product already in cart")
    void addItemToCart_DuplicateProductAccumulatesQuantity() {
        CartItem existingItem = CartItem.builder()
                .id(10L)
                .cart(cart)
                .productId(101L)
                .sku(activeProduct.getSku())
                .productName(activeProduct.getName())
                .unitPrice(activeProduct.getPrice())
                .quantity(2)
                .subtotal(new BigDecimal("100000.00"))
                .build();
        cart.getItems().add(existingItem);

        AddCartItemRequest request = AddCartItemRequest.builder()
                .productId(101L)
                .quantity(3)
                .build();

        when(productClient.getProductById(101L)).thenReturn(activeProduct);
        when(inventoryClient.getInventoryByProductId(101L)).thenReturn(availableInventory);
        when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArgument(0));
        when(cartMapper.toCartResponse(any(Cart.class))).thenReturn(new CartResponse());

        cartService.addItemToCart(userId, request);

        assertEquals(1, cart.getItems().size());
        assertEquals(5, existingItem.getQuantity());
        assertEquals(new BigDecimal("250000.00"), existingItem.getSubtotal());
    }

    @Test
    @DisplayName("addItemToCart - should throw InsufficientStockException when requested > available")
    void addItemToCart_ThrowsInsufficientStock() {
        AddCartItemRequest request = AddCartItemRequest.builder()
                .productId(101L)
                .quantity(15) // available is 10
                .build();

        when(productClient.getProductById(101L)).thenReturn(activeProduct);
        when(inventoryClient.getInventoryByProductId(101L)).thenReturn(availableInventory);
        when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));

        assertThrows(InsufficientStockException.class, () -> cartService.addItemToCart(userId, request));
    }

    @Test
    @DisplayName("addItemToCart - should throw ProductUnavailableException when product is INACTIVE")
    void addItemToCart_ThrowsProductInactive() {
        activeProduct.setStatus("INACTIVE");
        AddCartItemRequest request = AddCartItemRequest.builder()
                .productId(101L)
                .quantity(1)
                .build();

        when(productClient.getProductById(101L)).thenReturn(activeProduct);

        assertThrows(ProductUnavailableException.class, () -> cartService.addItemToCart(userId, request));
    }

    @Test
    @DisplayName("updateCartItemQuantity - should successfully update item quantity")
    void updateCartItemQuantity_Success() {
        CartItem existingItem = CartItem.builder()
                .id(10L)
                .cart(cart)
                .productId(101L)
                .quantity(2)
                .unitPrice(activeProduct.getPrice())
                .subtotal(new BigDecimal("100000.00"))
                .build();
        cart.getItems().add(existingItem);

        UpdateCartItemRequest request = UpdateCartItemRequest.builder()
                .quantity(4)
                .build();

        when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));
        when(inventoryClient.getInventoryByProductId(101L)).thenReturn(availableInventory);
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArgument(0));
        when(cartMapper.toCartResponse(any(Cart.class))).thenReturn(new CartResponse());

        cartService.updateCartItemQuantity(userId, 10L, request);

        assertEquals(4, existingItem.getQuantity());
        assertEquals(new BigDecimal("200000.00"), existingItem.getSubtotal());
    }

    @Test
    @DisplayName("clearCart - should empty all items in cart")
    void clearCart_Success() {
        CartItem item = CartItem.builder()
                .id(10L)
                .cart(cart)
                .productId(101L)
                .quantity(2)
                .subtotal(new BigDecimal("100000.00"))
                .build();
        cart.getItems().add(item);
        cart.setTotalAmount(new BigDecimal("100000.00"));

        when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArgument(0));
        when(cartMapper.toCartResponse(any(Cart.class))).thenAnswer(i -> {
            Cart c = i.getArgument(0);
            return CartResponse.builder().totalAmount(c.getTotalAmount()).build();
        });

        CartResponse response = cartService.clearCart(userId);

        assertEquals(BigDecimal.ZERO, response.getTotalAmount());
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    @DisplayName("validateCart - should detect price change and stock issues")
    void validateCart_DetectsIssues() {
        CartItem item = CartItem.builder()
                .id(10L)
                .cart(cart)
                .productId(101L)
                .productName("Diamond Gold Ring")
                .unitPrice(new BigDecimal("40000.00")) // Snapshot was 40,000
                .quantity(15) // Cart has 15, stock has 10
                .build();
        cart.getItems().add(item);

        when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(cart));
        when(productClient.getProductById(101L)).thenReturn(activeProduct); // Live price is 50,000
        when(inventoryClient.getInventoryByProductId(101L)).thenReturn(availableInventory); // Available stock is 10

        CartValidationResponse validation = cartService.validateCart(userId);

        assertFalse(validation.isValid());
        assertEquals(2, validation.getIssues().size());
    }
}
