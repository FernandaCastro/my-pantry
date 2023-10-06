package com.fcastro.pantryInventory.event;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseItemDto {
    long pantryId;
    String pantryName;

    long productId;
    String productDescription;
    String productSize;

    int purchase;
    boolean isPurchased;
}
