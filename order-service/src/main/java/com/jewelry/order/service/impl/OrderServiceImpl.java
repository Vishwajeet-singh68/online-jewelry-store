package com.jewelry.order.service.impl;

import com.jewelry.order.client.CartClient;
import com.jewelry.order.client.InventoryClient;
import com.jewelry.order.client.ProductClient;
import com.jewelry.order.client.dto.*;
import com.jewelry.order.dto.request.CreateOrderRequest;
import com.jewelry.order.dto.request.UpdateOrderStatusRequest;
import com.jewelry.order.dto.response.OrderResponse;
import com.jewelry.order.dto.response.PageResponse;
import com.jewelry.order.entity.Order;
import com.jewelry.order.entity.OrderIdempotency;
import com.jewelry.order.entity.OrderItem;
import com.jewelry.order.entity.ShippingAddress;
import com.jewelry.order.enums.OrderStatus;
import com.jewelry.order.enums.PaymentStatus;
import com.jewelry.order.exception.*;
import com.jewelry.order.mapper.OrderMapper;
import com.jewelry.order.repository.OrderIdempotencyRepository;
import com.jewelry.order.repository.OrderRepository;
import com.jewelry.order.service.OrderService;
import com.jewelry.order.service.OrderStatusManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderIdempotencyRepository idempotencyRepository;
    private final CartClient cartClient;
    private final InventoryClient inventoryClient;
    private final ProductClient productClient;
    private final OrderStatusManager statusManager;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse createOrder(Long userId, CreateOrderRequest request, String idempotencyKey) {
        // 1. Idempotency Check
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<OrderIdempotency> existingIdempotency = idempotencyRepository.findByUserIdAndIdempotencyKey(userId, idempotencyKey.trim());
            if (existingIdempotency.isPresent()) {
                Long existingOrderId = existingIdempotency.get().getOrderId();
                log.info("Idempotency match found for key={}. Returning existing orderId={}", idempotencyKey, existingOrderId);
                Order existingOrder = orderRepository.findByIdWithItems(existingOrderId)
                        .orElseThrow(() -> new OrderNotFoundException("Order not found for idempotency key record: " + existingOrderId));
                return orderMapper.toOrderResponse(existingOrder);
            }
        }

        // 2. Fetch User Cart
        CartClientDto cart = cartClient.getCart(userId);
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BadRequestException("Cannot place order: User cart is empty");
        }

        // 3. Pre-checkout Cart Validation
        CartValidationClientDto validation = cartClient.validateCart(userId);
        if (validation != null && !validation.isValid()) {
            String issueMsg = validation.getIssues().stream()
                    .map(i -> i.getType() + ": " + i.getMessage())
                    .collect(Collectors.joining(", "));
            throw new CartValidationException("Cart validation failed pre-checkout: " + issueMsg);
        }

        // 4. Reserve Inventory & Handle Compensating Actions on Failure
        List<ReserveStockRequest> reservedItems = new ArrayList<>();
        for (CartItemClientDto item : cart.getItems()) {
            ReserveStockRequest reserveReq = ReserveStockRequest.builder()
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .build();

            try {
                inventoryClient.reserveStock(reserveReq);
                reservedItems.add(reserveReq);
            } catch (Exception ex) {
                log.error("Stock reservation failed for productId={}. Rolling back previous reservations.", item.getProductId(), ex);
                // Compensating action: release all previously reserved items
                for (ReserveStockRequest releaseReq : reservedItems) {
                    try {
                        inventoryClient.releaseStock(releaseReq);
                    } catch (Exception rollbackEx) {
                        log.error("Failed to release stock during compensation for productId={}", releaseReq.getProductId(), rollbackEx);
                    }
                }
                throw new InventoryReservationException("Unable to place order: stock reservation failed for item '" + item.getProductName() + "'");
            }
        }

        // 5. Create Order & OrderItems Snapshots
        ShippingAddress shippingAddress = orderMapper.toShippingAddress(request.getShippingAddress());
        String orderNumber = generateUniqueOrderNumber();

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .userId(userId)
                .status(OrderStatus.CONFIRMED)
                .paymentStatus(PaymentStatus.PENDING)
                .shippingAddress(shippingAddress)
                .items(new ArrayList<>())
                .totalAmount(BigDecimal.ZERO)
                .build();

        for (CartItemClientDto item : cart.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productId(item.getProductId())
                    .sku(item.getSku())
                    .productName(item.getProductName())
                    .productImage(item.getProductImage())
                    .unitPrice(item.getUnitPrice())
                    .quantity(item.getQuantity())
                    .build();
            orderItem.recalculateSubtotal();
            order.addItem(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        // 6. Clear Cart
        try {
            cartClient.clearCart(userId);
        } catch (Exception ex) {
            log.warn("Failed to clear cart for userId={} after order creation", userId, ex);
        }

        // 7. Save Idempotency Mapping if key provided
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            OrderIdempotency idempotency = OrderIdempotency.builder()
                    .userId(userId)
                    .idempotencyKey(idempotencyKey.trim())
                    .orderId(savedOrder.getId())
                    .build();
            idempotencyRepository.save(idempotency);
        }

        log.info("Order successfully created: orderNumber={}, userId={}, totalAmount={}", savedOrder.getOrderNumber(), userId, savedOrder.getTotalAmount());
        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getUserOrders(Long userId, Pageable pageable, OrderStatus status) {
        Page<Order> page = (status != null)
                ? orderRepository.findByUserIdAndStatus(userId, status, pageable)
                : orderRepository.findByUserId(userId, pageable);

        List<OrderResponse> content = page.getContent().stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());

        return PageResponse.<OrderResponse>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getUserOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserIdWithItems(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserIdWithItems(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        if (!statusManager.canCancel(order.getStatus())) {
            throw new OrderCancellationException("Order cannot be cancelled in its current status: " + order.getStatus());
        }

        // Release reserved inventory
        for (OrderItem item : order.getItems()) {
            try {
                inventoryClient.releaseStock(ReserveStockRequest.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .build());
            } catch (Exception ex) {
                log.error("Failed to release inventory during cancellation for orderId={}, productId={}", orderId, item.getProductId(), ex);
            }
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);
        log.info("Order cancelled successfully: orderId={}, userId={}", orderId, userId);
        return orderMapper.toOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getAllOrders(Pageable pageable, OrderStatus status) {
        Page<Order> page = (status != null)
                ? orderRepository.findByStatus(status, pageable)
                : orderRepository.findAll(pageable);

        List<OrderResponse> content = page.getContent().stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());

        return PageResponse.<OrderResponse>builder()
                .content(content)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByIdAdmin(Long orderId) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatusAdmin(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        statusManager.validateTransition(order.getStatus(), request.getStatus());
        order.setStatus(request.getStatus());

        Order savedOrder = orderRepository.save(order);
        log.info("Admin updated status for orderId={} to {}", orderId, request.getStatus());
        return orderMapper.toOrderResponse(savedOrder);
    }

    private String generateUniqueOrderNumber() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "ORD-" + dateStr + "-" + randomSuffix;
    }
}
