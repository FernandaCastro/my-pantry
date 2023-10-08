package com.fcastro.pantryService.pantryItem;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PantryItemConsumedDto {
    Long pantryId;
    Long productId;
    int qty;
}
