package com.fcastro.security.modelclient;

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
