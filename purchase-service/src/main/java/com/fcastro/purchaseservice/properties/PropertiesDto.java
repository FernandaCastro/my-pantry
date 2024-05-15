package com.fcastro.purchaseservice.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertiesDto {

    private String propertyKey;

    @NotBlank(message = "Property Value is mandatory")
    private String propertyValue;

}
