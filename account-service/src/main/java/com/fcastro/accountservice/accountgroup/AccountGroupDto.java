package com.fcastro.accountservice.accountgroup;

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
