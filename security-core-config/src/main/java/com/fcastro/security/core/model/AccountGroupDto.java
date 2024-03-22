package com.fcastro.security.core.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountGroupDto {

    private Long id;
    private String name;
    private AccountGroupDto parentAccountGroup;
}
