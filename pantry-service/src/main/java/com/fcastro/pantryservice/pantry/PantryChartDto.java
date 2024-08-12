package com.fcastro.pantryservice.pantry;

import com.fcastro.security.core.model.AccountGroupDto;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PantryChartDto {

    private Long id;
    private String name;
    private String type;
    private Boolean isActive;
    private double percentage;

    private List<PantryItemChartDto> criticalItems;
    private AccountGroupDto accountGroup;
}

