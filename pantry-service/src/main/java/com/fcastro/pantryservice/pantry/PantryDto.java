package com.fcastro.pantryservice.pantry;

import com.fcastro.pantryservice.pantryItem.PantryItemDto;
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

    private Long accountGroupId;

    public PantryDto(Long id, String name, String type, Boolean isActive, Long accountGroupId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.isActive = isActive;
        this.accountGroupId = accountGroupId;
    }
}
