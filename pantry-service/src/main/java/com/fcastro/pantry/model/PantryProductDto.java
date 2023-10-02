package com.fcastro.pantry.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PantryProductDto {

    private int idealQty;
    private int currentQty;

    private PantryDto pantry;
    private ProductDto product;
}

