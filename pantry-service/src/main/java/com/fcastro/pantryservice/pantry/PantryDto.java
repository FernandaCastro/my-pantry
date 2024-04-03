package com.fcastro.pantryservice.pantry;

import com.fcastro.pantryservice.pantryitem.PantryItemDto;
import com.fcastro.security.core.model.AccountGroupDto;
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

    private AccountGroupDto accountGroup;
}
