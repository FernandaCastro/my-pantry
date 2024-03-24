package com.fcastro.pantryservice.pantryitem;

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
