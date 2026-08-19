package com.jewelry.inventory.mapper;

import com.jewelry.inventory.dto.request.CreateInventoryRequest;
import com.jewelry.inventory.dto.response.InventoryResponse;
import com.jewelry.inventory.entity.Inventory;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-20T01:11:54+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.46.100.v20260624-0231, environment: Java 21.0.11 (Eclipse Adoptium)"
)
@Component
public class InventoryMapperImpl implements InventoryMapper {

    @Override
    public Inventory toEntity(CreateInventoryRequest request) {
        if ( request == null ) {
            return null;
        }

        Inventory.InventoryBuilder inventory = Inventory.builder();

        inventory.availableQuantity( request.getQuantity() );
        inventory.lowStockThreshold( request.getLowStockThreshold() );
        inventory.productId( request.getProductId() );
        inventory.sku( request.getSku() );

        return inventory.build();
    }

    @Override
    public InventoryResponse toResponse(Inventory inventory) {
        if ( inventory == null ) {
            return null;
        }

        InventoryResponse.InventoryResponseBuilder inventoryResponse = InventoryResponse.builder();

        inventoryResponse.availableQuantity( inventory.getAvailableQuantity() );
        inventoryResponse.createdAt( inventory.getCreatedAt() );
        inventoryResponse.id( inventory.getId() );
        inventoryResponse.lowStockThreshold( inventory.getLowStockThreshold() );
        inventoryResponse.productId( inventory.getProductId() );
        inventoryResponse.reservedQuantity( inventory.getReservedQuantity() );
        inventoryResponse.sku( inventory.getSku() );
        inventoryResponse.soldQuantity( inventory.getSoldQuantity() );
        inventoryResponse.status( inventory.getStatus() );
        inventoryResponse.updatedAt( inventory.getUpdatedAt() );

        return inventoryResponse.build();
    }
}
