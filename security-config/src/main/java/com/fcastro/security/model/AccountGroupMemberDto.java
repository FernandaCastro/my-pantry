package com.fcastro.security.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountGroupMemberDto {

    private Long accountGroupId;
    private Long accountId;

    private RoleDto role;
    private AccountGroupDto accountGroup;
    private AccountDto account;
}
