package com.fcastro.pantryservice.pantryitem;

import com.fcastro.pantryservice.pantry.PantryDto;
import com.fcastro.pantryservice.product.ProductDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PantryItemDto {

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

        if (!getPantry().getId().equals(that.getPantry().getId())) return false;
        return getProduct().getId().equals(that.getProduct().getId());
    }

    @Override
    public int hashCode() {
        int result = getPantry().getId().hashCode();
        result = 31 * result + getProduct().getId().hashCode();
        return result;
    }
}

