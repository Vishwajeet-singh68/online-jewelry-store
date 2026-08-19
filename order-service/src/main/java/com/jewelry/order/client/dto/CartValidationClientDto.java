package com.jewelry.order.client.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartValidationClientDto {
    private boolean valid;
    private List<CartValidationIssueDto> issues;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CartValidationIssueDto {
        private Long productId;
        private String type;
        private String message;
    }
}
