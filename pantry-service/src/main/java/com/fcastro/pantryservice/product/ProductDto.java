package com.fcastro.pantryservice.product;

import com.fcastro.security.modelclient.AccountGroupDto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductDto {

    private Long id;
    @NotBlank(message = "Code is mandatory")
    private String code;
    private String description;
    private String size;
    private String category;
    private AccountGroupDto accountGroup;
}
