package com.jewelry.order.mapper;

import com.jewelry.order.dto.request.ShippingAddressRequest;
import com.jewelry.order.dto.response.OrderItemResponse;
import com.jewelry.order.dto.response.OrderResponse;
import com.jewelry.order.dto.response.ShippingAddressResponse;
import com.jewelry.order.entity.Order;
import com.jewelry.order.entity.OrderItem;
import com.jewelry.order.entity.ShippingAddress;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    ShippingAddress toShippingAddress(ShippingAddressRequest request);

    ShippingAddressResponse toShippingAddressResponse(ShippingAddress shippingAddress);

    OrderItemResponse toOrderItemResponse(OrderItem orderItem);

    OrderResponse toOrderResponse(Order order);
}
