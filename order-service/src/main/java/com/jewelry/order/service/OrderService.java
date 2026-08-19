package com.jewelry.order.service;

import com.jewelry.order.dto.request.CreateOrderRequest;
import com.jewelry.order.dto.request.UpdateOrderStatusRequest;
import com.jewelry.order.dto.response.OrderResponse;
import com.jewelry.order.dto.response.PageResponse;
import com.jewelry.order.enums.OrderStatus;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse createOrder(Long userId, CreateOrderRequest request, String idempotencyKey);

    PageResponse<OrderResponse> getUserOrders(Long userId, Pageable pageable, OrderStatus status);

    OrderResponse getUserOrderById(Long userId, Long orderId);

    OrderResponse cancelOrder(Long userId, Long orderId);

    PageResponse<OrderResponse> getAllOrders(Pageable pageable, OrderStatus status);

    OrderResponse getOrderByIdAdmin(Long orderId);

    OrderResponse updateOrderStatusAdmin(Long orderId, UpdateOrderStatusRequest request);
}
