package com.fcastro.app.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductDto {

    private long id;
    @NotBlank(message = "Code is mandatory")
    private String code;
    private String description;
    private String size;
    private String category;
    private Long accountGroupId;
}
