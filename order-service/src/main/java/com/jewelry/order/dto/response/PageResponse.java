package com.jewelry.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Generic paginated response wrapper")
public class PageResponse<T> {

    @Schema(description = "Page content items")
    private List<T> content;

    @Schema(description = "Current page number (0-indexed)", example = "0")
    private int pageNumber;

    @Schema(description = "Page size limit", example = "20")
    private int pageSize;

    @Schema(description = "Total number of items across all pages", example = "45")
    private long totalElements;

    @Schema(description = "Total number of pages available", example = "3")
    private int totalPages;

    @Schema(description = "Is this the last page?", example = "false")
    private boolean last;
}
