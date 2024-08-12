package com.fcastro.pantryservice.pantry;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PantryItemChartDto {
    private Long productId;
    private String productCode;
    private int idealQty;
    private int currentQty;
    private double percentage;
}
