package com.jewelry.cart.mapper;

import com.jewelry.cart.dto.response.CartItemResponse;
import com.jewelry.cart.dto.response.CartResponse;
import com.jewelry.cart.entity.Cart;
import com.jewelry.cart.entity.CartItem;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-20T01:11:50+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class CartMapperImpl implements CartMapper {

    @Override
    public CartItemResponse toCartItemResponse(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }

        CartItemResponse.CartItemResponseBuilder cartItemResponse = CartItemResponse.builder();

        cartItemResponse.id( cartItem.getId() );
        cartItemResponse.productId( cartItem.getProductId() );
        cartItemResponse.productImage( cartItem.getProductImage() );
        cartItemResponse.productName( cartItem.getProductName() );
        cartItemResponse.quantity( cartItem.getQuantity() );
        cartItemResponse.sku( cartItem.getSku() );
        cartItemResponse.subtotal( cartItem.getSubtotal() );
        cartItemResponse.unitPrice( cartItem.getUnitPrice() );

        return cartItemResponse.build();
    }

    @Override
    public CartResponse toCartResponse(Cart cart) {
        if ( cart == null ) {
            return null;
        }

        CartResponse.CartResponseBuilder cartResponse = CartResponse.builder();

        cartResponse.createdAt( cart.getCreatedAt() );
        cartResponse.id( cart.getId() );
        cartResponse.items( cartItemListToCartItemResponseList( cart.getItems() ) );
        cartResponse.totalAmount( cart.getTotalAmount() );
        cartResponse.updatedAt( cart.getUpdatedAt() );
        cartResponse.userId( cart.getUserId() );

        return cartResponse.build();
    }

    protected List<CartItemResponse> cartItemListToCartItemResponseList(List<CartItem> list) {
        if ( list == null ) {
            return null;
        }

        List<CartItemResponse> list1 = new ArrayList<CartItemResponse>( list.size() );
        for ( CartItem cartItem : list ) {
            list1.add( toCartItemResponse( cartItem ) );
        }

        return list1;
    }
}
