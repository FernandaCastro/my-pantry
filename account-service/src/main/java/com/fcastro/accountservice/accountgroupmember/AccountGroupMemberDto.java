package com.fcastro.accountservice.accountgroupmember;

import com.fcastro.accountservice.account.AccountDto;
import com.fcastro.accountservice.accountgroup.AccountGroupDto;
import com.fcastro.accountservice.role.RoleDto;
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
