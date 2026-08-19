package com.jewelry.cart.service.impl;

import com.jewelry.cart.client.InventoryClient;
import com.jewelry.cart.client.ProductClient;
import com.jewelry.cart.client.dto.InventoryClientDto;
import com.jewelry.cart.client.dto.ProductClientDto;
import com.jewelry.cart.dto.request.AddCartItemRequest;
import com.jewelry.cart.dto.request.UpdateCartItemRequest;
import com.jewelry.cart.dto.response.CartResponse;
import com.jewelry.cart.dto.response.CartValidationIssue;
import com.jewelry.cart.dto.response.CartValidationResponse;
import com.jewelry.cart.entity.Cart;
import com.jewelry.cart.entity.CartItem;
import com.jewelry.cart.exception.*;
import com.jewelry.cart.mapper.CartMapper;
import com.jewelry.cart.repository.CartItemRepository;
import com.jewelry.cart.repository.CartRepository;
import com.jewelry.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;
    private final CartMapper cartMapper;

    @Override
    @Transactional
    public CartResponse getCartByUserId(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return cartMapper.toCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addItemToCart(Long userId, AddCartItemRequest request) {
        // 1. Fetch Product details from Product Service
        ProductClientDto product = productClient.getProductById(request.getProductId());
        if (product == null) {
            throw new ProductUnavailableException("Product with ID " + request.getProductId() + " does not exist");
        }
        if (!"ACTIVE".equalsIgnoreCase(product.getStatus())) {
            throw new ProductUnavailableException("Product '" + product.getName() + "' is currently inactive and cannot be added to cart");
        }

        // 2. Fetch stock details from Inventory Service
        InventoryClientDto inventory = inventoryClient.getInventoryByProductId(request.getProductId());
        int availableStock = (inventory != null && inventory.getAvailableQuantity() != null)
                ? inventory.getAvailableQuantity() : 0;

        // 3. Get or create user's cart
        Cart cart = getOrCreateCart(userId);

        // 4. Check if product already exists in cart
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();

        int targetQuantity = request.getQuantity();
        if (existingItemOpt.isPresent()) {
            targetQuantity += existingItemOpt.get().getQuantity();
        }

        // 5. Stock Validation
        if (targetQuantity > availableStock) {
            throw new InsufficientStockException(
                    "Insufficient stock available for '" + product.getName() + "'. Requested total: "
                            + targetQuantity + ", Available: " + availableStock);
        }

        // 6. Add or update cart item
        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(targetQuantity);
            existingItem.setUnitPrice(product.getPrice());
            existingItem.recalculateSubtotal();
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(product.getId())
                    .sku(product.getSku())
                    .productName(product.getName())
                    .productImage(product.getImageUrl())
                    .unitPrice(product.getPrice())
                    .quantity(request.getQuantity())
                    .build();
            newItem.recalculateSubtotal();
            cart.addItem(newItem);
        }

        cart.recalculateTotal();
        Cart savedCart = cartRepository.save(cart);
        log.info("Added productId={} to userId={} cart. Total items: {}", request.getProductId(), userId, savedCart.getItems().size());
        return cartMapper.toCartResponse(savedCart);
    }

    @Override
    @Transactional
    public CartResponse updateCartItemQuantity(Long userId, Long itemId, UpdateCartItemRequest request) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user ID: " + userId));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException("Cart item ID " + itemId + " not found in user's cart"));

        // Validate inventory
        InventoryClientDto inventory = inventoryClient.getInventoryByProductId(item.getProductId());
        int availableStock = (inventory != null && inventory.getAvailableQuantity() != null)
                ? inventory.getAvailableQuantity() : 0;

        if (request.getQuantity() > availableStock) {
            throw new InsufficientStockException(
                    "Insufficient stock available. Requested: " + request.getQuantity() + ", Available: " + availableStock);
        }

        item.setQuantity(request.getQuantity());
        item.recalculateSubtotal();
        cart.recalculateTotal();

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toCartResponse(savedCart);
    }

    @Override
    @Transactional
    public CartResponse removeItemFromCart(Long userId, Long itemId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user ID: " + userId));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException("Cart item ID " + itemId + " not found in user's cart"));

        cart.removeItem(item);
        Cart savedCart = cartRepository.save(cart);
        log.info("Removed itemId={} from userId={} cart", itemId, userId);
        return cartMapper.toCartResponse(savedCart);
    }

    @Override
    @Transactional
    public CartResponse clearCart(Long userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseGet(() -> getOrCreateCart(userId));

        cart.clearItems();
        Cart savedCart = cartRepository.save(cart);
        log.info("Cleared cart for userId={}", userId);
        return cartMapper.toCartResponse(savedCart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartValidationResponse validateCart(Long userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseGet(() -> getOrCreateCart(userId));

        List<CartValidationIssue> issues = new ArrayList<>();

        for (CartItem item : cart.getItems()) {
            try {
                // 1. Check Product Service
                ProductClientDto product = productClient.getProductById(item.getProductId());
                if (product == null) {
                    issues.add(CartValidationIssue.builder()
                            .productId(item.getProductId())
                            .type("PRODUCT_NOT_FOUND")
                            .message("Product is no longer available in store catalog")
                            .build());
                    continue;
                }

                if (!"ACTIVE".equalsIgnoreCase(product.getStatus())) {
                    issues.add(CartValidationIssue.builder()
                            .productId(item.getProductId())
                            .type("PRODUCT_INACTIVE")
                            .message("Product '" + item.getProductName() + "' is currently inactive")
                            .build());
                }

                if (product.getPrice() != null && product.getPrice().compareTo(item.getUnitPrice()) != 0) {
                    issues.add(CartValidationIssue.builder()
                            .productId(item.getProductId())
                            .type("PRICE_CHANGED")
                            .message("Price changed from " + item.getUnitPrice() + " to " + product.getPrice())
                            .build());
                }

                // 2. Check Inventory Service
                InventoryClientDto inventory = inventoryClient.getInventoryByProductId(item.getProductId());
                int available = (inventory != null && inventory.getAvailableQuantity() != null)
                        ? inventory.getAvailableQuantity() : 0;

                if (item.getQuantity() > available) {
                    issues.add(CartValidationIssue.builder()
                            .productId(item.getProductId())
                            .type("INSUFFICIENT_STOCK")
                            .message("Only " + available + " units available for '" + item.getProductName() + "'")
                            .build());
                }

            } catch (Exception e) {
                issues.add(CartValidationIssue.builder()
                        .productId(item.getProductId())
                        .type("SERVICE_ERROR")
                        .message("Unable to validate item due to downstream service issue")
                        .build());
            }
        }

        return CartValidationResponse.builder()
                .valid(issues.isEmpty())
                .issues(issues)
                .build();
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserIdWithItems(userId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .userId(userId)
                            .totalAmount(BigDecimal.ZERO)
                            .items(new ArrayList<>())
                            .build();
                    return cartRepository.save(newCart);
                });
    }
}
