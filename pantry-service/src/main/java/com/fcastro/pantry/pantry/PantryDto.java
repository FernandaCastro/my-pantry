package com.fcastro.pantry.pantry;

import com.fcastro.pantry.pantryItem.PantryItemDto;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PantryDto {

    private Long id;
    private String name;
    private String type;
    private Boolean isActive;

    private List<PantryItemDto> products;
}
