package com.fcastro.security.model;

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
