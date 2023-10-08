package com.fcastro.purchaseService.purchaseItem;

import com.fcastro.purchaseService.purchase.PurchaseDto;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseItemDto {

    Long id;
    Long pantryId;
    String pantryName;

    Long productId;
    String productDescription;
    String productSize;

    int quantity;
    boolean isPurchased;

    PurchaseDto purchase;
}
