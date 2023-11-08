package com.fcastro.purchase.purchaseItem;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fcastro.purchase.purchase.PurchaseDto;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseItemDto {

    private Long id;
    private Long pantryId;
    private String pantryName;

    private Long productId;
    private String productDescription;
    private String productSize;

    private int qtyProvisioned;
    private int qtyPurchased;

    @JsonIgnoreProperties("items")
    private PurchaseDto purchase;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PurchaseItemDto that)) return false;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
