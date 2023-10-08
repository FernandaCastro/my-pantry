package com.fcastro.pantryService.pantryItem;

import com.fcastro.pantryService.pantry.PantryDto;
import com.fcastro.pantryService.product.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PantryItemDto {

    private Long pantryId;
    private Long productId;
    private int idealQty;
    private int currentQty;
    private int provisionedQty;
    private LocalDateTime lastProvisioning;

    private PantryDto pantry;
    private ProductDto product;
}

