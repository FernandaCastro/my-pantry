package com.fcastro.pantry.pantryItem;

import com.fcastro.model.ProductDto;
import com.fcastro.pantry.pantry.PantryDto;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PantryItemDto)) return false;

        PantryItemDto that = (PantryItemDto) o;

        if (!getPantryId().equals(that.getPantryId())) return false;
        return getProductId().equals(that.getProductId());
    }

    @Override
    public int hashCode() {
        int result = getPantryId().hashCode();
        result = 31 * result + getProductId().hashCode();
        return result;
    }
}

