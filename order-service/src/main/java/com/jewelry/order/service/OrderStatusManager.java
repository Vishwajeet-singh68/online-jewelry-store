package com.jewelry.order.service;

import com.jewelry.order.enums.OrderStatus;
import com.jewelry.order.exception.InvalidOrderStatusException;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
public class OrderStatusManager {

    private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = Map.of(
            OrderStatus.PENDING, EnumSet.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED, OrderStatus.FAILED),
            OrderStatus.CONFIRMED, EnumSet.of(OrderStatus.PROCESSING, OrderStatus.CANCELLED, OrderStatus.FAILED),
            OrderStatus.PROCESSING, EnumSet.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
            OrderStatus.SHIPPED, EnumSet.of(OrderStatus.DELIVERED),
            OrderStatus.DELIVERED, EnumSet.noneOf(OrderStatus.class),
            OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class),
            OrderStatus.FAILED, EnumSet.noneOf(OrderStatus.class)
    );

    public void validateTransition(OrderStatus currentStatus, OrderStatus targetStatus) {
        if (currentStatus == targetStatus) {
            return; // No change required
        }

        Set<OrderStatus> allowed = VALID_TRANSITIONS.getOrDefault(currentStatus, EnumSet.noneOf(OrderStatus.class));
        if (!allowed.contains(targetStatus)) {
            throw new InvalidOrderStatusException(
                    "Invalid order status transition from '" + currentStatus + "' to '" + targetStatus + "'");
        }
    }

    public boolean canCancel(OrderStatus currentStatus) {
        return currentStatus == OrderStatus.PENDING ||
               currentStatus == OrderStatus.CONFIRMED ||
               currentStatus == OrderStatus.PROCESSING;
    }
}
