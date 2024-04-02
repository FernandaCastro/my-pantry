package com.fcastro.security.core.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccessControlDto {

    @NotBlank(message = "Class is mandatory")
    private String clazz;

    @NotBlank(message = "Class Id is mandatory")
    private Long clazzId;

    @NotBlank(message = "Account Group is mandatory")
    private AccountGroupDto accountGroup;
}
