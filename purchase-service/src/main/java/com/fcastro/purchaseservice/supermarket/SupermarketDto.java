package com.fcastro.purchaseservice.supermarket;

import com.fcastro.security.core.model.AccountGroupDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupermarketDto {

    private Long id;

    @NotBlank(message = "Name is mandatory.")
    private String name;

    @NotNull(message = "Categories is mandatory.")
    private ArrayList<String> categories;

    private AccountGroupDto accountGroup;
}
