package com.jewelry.order.service.impl;

import com.jewelry.order.client.CartClient;
import com.jewelry.order.client.InventoryClient;
import com.jewelry.order.client.ProductClient;
import com.jewelry.order.client.dto.*;
import com.jewelry.order.dto.request.CreateOrderRequest;
import com.jewelry.order.dto.request.ShippingAddressRequest;
import com.jewelry.order.dto.request.UpdateOrderStatusRequest;
import com.jewelry.order.dto.response.OrderResponse;
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
import com.jewelry.order.service.OrderStatusManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderIdempotencyRepository idempotencyRepository;

    @Mock
    private CartClient cartClient;

    @Mock
    private InventoryClient inventoryClient;

    @Mock
    private ProductClient productClient;

    @Spy
    private OrderStatusManager statusManager = new OrderStatusManager();

    @Spy
    private OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    @InjectMocks
    private OrderServiceImpl orderService;

    private Long userId;
    private CreateOrderRequest createOrderRequest;
    private CartClientDto cartClientDto;
    private CartItemClientDto cartItemClientDto;

    @BeforeEach
    void setUp() {
        userId = 101L;

        ShippingAddressRequest addressReq = ShippingAddressRequest.builder()
                .fullName("John Doe")
                .phoneNumber("9876543210")
                .addressLine1("123 Main Street")
                .addressLine2("Apt 4")
                .city("Mathura")
                .state("Uttar Pradesh")
                .postalCode("281001")
                .country("India")
                .build();

        createOrderRequest = CreateOrderRequest.builder()
                .shippingAddress(addressReq)
                .build();

        cartItemClientDto = CartItemClientDto.builder()
                .id(1L)
                .productId(201L)
                .sku("JW-RING-001")
                .productName("Gold Diamond Ring")
                .productImage("http://img.png")
                .unitPrice(new BigDecimal("50000.00"))
                .quantity(2)
                .subtotal(new BigDecimal("100000.00"))
                .build();

        cartClientDto = CartClientDto.builder()
                .id(10L)
                .userId(userId)
                .items(List.of(cartItemClientDto))
                .totalAmount(new BigDecimal("100000.00"))
                .build();
    }

    @Test
    @DisplayName("Create Order - Success Flow")
    void createOrder_Success() {
        when(idempotencyRepository.findByUserIdAndIdempotencyKey(anyLong(), anyString())).thenReturn(Optional.empty());
        when(cartClient.getCart(userId)).thenReturn(cartClientDto);
        when(cartClient.validateCart(userId)).thenReturn(CartValidationClientDto.builder().valid(true).issues(Collections.emptyList()).build());
        when(inventoryClient.reserveStock(any())).thenReturn(InventoryClientDto.builder().build());
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(1L);
            return o;
        });

        OrderResponse response = orderService.createOrder(userId, createOrderRequest, "key-123");

        assertNotNull(response);
        assertEquals(userId, response.getUserId());
        assertEquals(OrderStatus.CONFIRMED, response.getStatus());
        assertEquals(PaymentStatus.PENDING, response.getPaymentStatus());
        assertEquals(new BigDecimal("100000.00"), response.getTotalAmount());
        assertEquals(1, response.getItems().size());

        verify(inventoryClient, times(1)).reserveStock(any());
        verify(cartClient, times(1)).clearCart(userId);
        verify(idempotencyRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Create Order - Empty Cart Throws BadRequestException")
    void createOrder_EmptyCart_ThrowsBadRequest() {
        when(cartClient.getCart(userId)).thenReturn(CartClientDto.builder().items(Collections.emptyList()).build());

        assertThrows(BadRequestException.class, () -> orderService.createOrder(userId, createOrderRequest, null));
    }

    @Test
    @DisplayName("Create Order - Cart Validation Fails Throws CartValidationException")
    void createOrder_CartValidationFails_ThrowsException() {
        when(cartClient.getCart(userId)).thenReturn(cartClientDto);
        CartValidationClientDto validation = CartValidationClientDto.builder()
                .valid(false)
                .issues(List.of(CartValidationClientDto.CartValidationIssueDto.builder().type("STOCK").message("Out of stock").build()))
                .build();
        when(cartClient.validateCart(userId)).thenReturn(validation);

        assertThrows(CartValidationException.class, () -> orderService.createOrder(userId, createOrderRequest, null));
    }

    @Test
    @DisplayName("Create Order - Stock Reservation Fails Triggers Compensation")
    void createOrder_InventoryReservationFails_TriggersCompensation() {
        CartItemClientDto item1 = CartItemClientDto.builder().productId(101L).quantity(1).unitPrice(new BigDecimal("100")).subtotal(new BigDecimal("100")).build();
        CartItemClientDto item2 = CartItemClientDto.builder().productId(102L).quantity(1).unitPrice(new BigDecimal("200")).subtotal(new BigDecimal("200")).build();
        cartClientDto.setItems(List.of(item1, item2));

        when(cartClient.getCart(userId)).thenReturn(cartClientDto);
        when(cartClient.validateCart(userId)).thenReturn(CartValidationClientDto.builder().valid(true).issues(Collections.emptyList()).build());

        when(inventoryClient.reserveStock(argThat(req -> req.getProductId().equals(101L)))).thenReturn(InventoryClientDto.builder().build());
        doThrow(new RuntimeException("Stock unavailable")).when(inventoryClient).reserveStock(argThat(req -> req.getProductId().equals(102L)));

        assertThrows(InventoryReservationException.class, () -> orderService.createOrder(userId, createOrderRequest, null));

        // Verify compensation released item 101 stock
        verify(inventoryClient, times(1)).releaseStock(argThat(req -> req.getProductId().equals(101L)));
    }

    @Test
    @DisplayName("Create Order - Idempotency Match Returns Existing Order")
    void createOrder_IdempotencyMatch_ReturnsExistingOrder() {
        OrderIdempotency idempotency = OrderIdempotency.builder()
                .userId(userId)
                .idempotencyKey("dup-key")
                .orderId(1L)
                .build();
        Order existingOrder = Order.builder()
                .id(1L)
                .orderNumber("ORD-20260819-123456")
                .userId(userId)
                .status(OrderStatus.CONFIRMED)
                .paymentStatus(PaymentStatus.PENDING)
                .totalAmount(new BigDecimal("100000.00"))
                .items(Collections.emptyList())
                .build();

        when(idempotencyRepository.findByUserIdAndIdempotencyKey(userId, "dup-key")).thenReturn(Optional.of(idempotency));
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(existingOrder));

        OrderResponse response = orderService.createOrder(userId, createOrderRequest, "dup-key");

        assertNotNull(response);
        assertEquals("ORD-20260819-123456", response.getOrderNumber());
        verify(cartClient, never()).getCart(anyLong());
    }

    @Test
    @DisplayName("Cancel Order - Success Releases Stock")
    void cancelOrder_Success() {
        OrderItem item = OrderItem.builder().id(1L).productId(201L).quantity(2).unitPrice(new BigDecimal("100")).subtotal(new BigDecimal("200")).build();
        Order order = Order.builder()
                .id(10L)
                .userId(userId)
                .status(OrderStatus.CONFIRMED)
                .items(List.of(item))
                .build();

        when(orderRepository.findByIdAndUserIdWithItems(10L, userId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        OrderResponse response = orderService.cancelOrder(userId, 10L);

        assertEquals(OrderStatus.CANCELLED, response.getStatus());
        verify(inventoryClient, times(1)).releaseStock(argThat(req -> req.getProductId().equals(201L) && req.getQuantity().equals(2)));
    }

    @Test
    @DisplayName("Cancel Order - Invalid Status Throws OrderCancellationException")
    void cancelOrder_InvalidStatus_ThrowsException() {
        Order order = Order.builder()
                .id(10L)
                .userId(userId)
                .status(OrderStatus.SHIPPED)
                .build();

        when(orderRepository.findByIdAndUserIdWithItems(10L, userId)).thenReturn(Optional.of(order));

        assertThrows(OrderCancellationException.class, () -> orderService.cancelOrder(userId, 10L));
    }

    @Test
    @DisplayName("Admin Update Status - Invalid Transition Throws InvalidOrderStatusException")
    void adminUpdateStatus_InvalidTransition_ThrowsException() {
        Order order = Order.builder()
                .id(10L)
                .status(OrderStatus.DELIVERED)
                .build();

        when(orderRepository.findByIdWithItems(10L)).thenReturn(Optional.of(order));

        UpdateOrderStatusRequest updateReq = UpdateOrderStatusRequest.builder().status(OrderStatus.CONFIRMED).build();

        assertThrows(InvalidOrderStatusException.class, () -> orderService.updateOrderStatusAdmin(10L, updateReq));
    }
}
