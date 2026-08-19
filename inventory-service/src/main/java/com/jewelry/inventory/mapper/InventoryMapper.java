package com.jewelry.inventory.mapper;

import com.jewelry.inventory.dto.request.CreateInventoryRequest;
import com.jewelry.inventory.dto.response.InventoryResponse;
import com.jewelry.inventory.entity.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "availableQuantity", source = "quantity")
    @Mapping(target = "reservedQuantity", ignore = true)
    @Mapping(target = "soldQuantity", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Inventory toEntity(CreateInventoryRequest request);

    InventoryResponse toResponse(Inventory inventory);
}
