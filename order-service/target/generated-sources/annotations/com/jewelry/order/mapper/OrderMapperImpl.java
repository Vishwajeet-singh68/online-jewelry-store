package com.jewelry.order.mapper;

import com.jewelry.order.dto.request.ShippingAddressRequest;
import com.jewelry.order.dto.response.OrderItemResponse;
import com.jewelry.order.dto.response.OrderResponse;
import com.jewelry.order.dto.response.ShippingAddressResponse;
import com.jewelry.order.entity.Order;
import com.jewelry.order.entity.OrderItem;
import com.jewelry.order.entity.ShippingAddress;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-20T01:12:00+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public ShippingAddress toShippingAddress(ShippingAddressRequest request) {
        if ( request == null ) {
            return null;
        }

        ShippingAddress.ShippingAddressBuilder shippingAddress = ShippingAddress.builder();

        shippingAddress.addressLine1( request.getAddressLine1() );
        shippingAddress.addressLine2( request.getAddressLine2() );
        shippingAddress.city( request.getCity() );
        shippingAddress.country( request.getCountry() );
        shippingAddress.fullName( request.getFullName() );
        shippingAddress.phoneNumber( request.getPhoneNumber() );
        shippingAddress.postalCode( request.getPostalCode() );
        shippingAddress.state( request.getState() );

        return shippingAddress.build();
    }

    @Override
    public ShippingAddressResponse toShippingAddressResponse(ShippingAddress shippingAddress) {
        if ( shippingAddress == null ) {
            return null;
        }

        ShippingAddressResponse.ShippingAddressResponseBuilder shippingAddressResponse = ShippingAddressResponse.builder();

        shippingAddressResponse.addressLine1( shippingAddress.getAddressLine1() );
        shippingAddressResponse.addressLine2( shippingAddress.getAddressLine2() );
        shippingAddressResponse.city( shippingAddress.getCity() );
        shippingAddressResponse.country( shippingAddress.getCountry() );
        shippingAddressResponse.fullName( shippingAddress.getFullName() );
        shippingAddressResponse.phoneNumber( shippingAddress.getPhoneNumber() );
        shippingAddressResponse.postalCode( shippingAddress.getPostalCode() );
        shippingAddressResponse.state( shippingAddress.getState() );

        return shippingAddressResponse.build();
    }

    @Override
    public OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        if ( orderItem == null ) {
            return null;
        }

        OrderItemResponse.OrderItemResponseBuilder orderItemResponse = OrderItemResponse.builder();

        orderItemResponse.id( orderItem.getId() );
        orderItemResponse.productId( orderItem.getProductId() );
        orderItemResponse.productImage( orderItem.getProductImage() );
        orderItemResponse.productName( orderItem.getProductName() );
        orderItemResponse.quantity( orderItem.getQuantity() );
        orderItemResponse.sku( orderItem.getSku() );
        orderItemResponse.subtotal( orderItem.getSubtotal() );
        orderItemResponse.unitPrice( orderItem.getUnitPrice() );

        return orderItemResponse.build();
    }

    @Override
    public OrderResponse toOrderResponse(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderResponse.OrderResponseBuilder orderResponse = OrderResponse.builder();

        orderResponse.createdAt( order.getCreatedAt() );
        orderResponse.id( order.getId() );
        orderResponse.items( orderItemListToOrderItemResponseList( order.getItems() ) );
        orderResponse.orderNumber( order.getOrderNumber() );
        orderResponse.paymentStatus( order.getPaymentStatus() );
        orderResponse.shippingAddress( toShippingAddressResponse( order.getShippingAddress() ) );
        orderResponse.status( order.getStatus() );
        orderResponse.totalAmount( order.getTotalAmount() );
        orderResponse.updatedAt( order.getUpdatedAt() );
        orderResponse.userId( order.getUserId() );

        return orderResponse.build();
    }

    protected List<OrderItemResponse> orderItemListToOrderItemResponseList(List<OrderItem> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItemResponse> list1 = new ArrayList<OrderItemResponse>( list.size() );
        for ( OrderItem orderItem : list ) {
            list1.add( toOrderItemResponse( orderItem ) );
        }

        return list1;
    }
}
