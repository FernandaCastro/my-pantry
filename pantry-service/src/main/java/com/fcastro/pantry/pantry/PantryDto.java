package com.fcastro.pantry.pantry;

import com.fcastro.pantry.pantryItem.PantryItemDto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PantryDto {

    private Long id;
    @NotBlank(message = "Name is mandatory")
    private String name;
    @NotBlank(message = "Type is mandatory")
    private String type;
    private Boolean isActive;

    private List<PantryItemDto> items;
}
