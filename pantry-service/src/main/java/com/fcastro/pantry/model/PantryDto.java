package com.fcastro.pantry.model;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PantryDto {

    private long id;
    private String name;
    private String type;
    private boolean active;

    private List<PantryProductDto> products;
}
