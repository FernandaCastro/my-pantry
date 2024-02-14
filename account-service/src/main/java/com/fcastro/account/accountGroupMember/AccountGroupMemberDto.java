package com.fcastro.account.accountGroupMember;

import com.fcastro.account.account.Account;
import com.fcastro.account.accountGroup.AccountGroup;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountGroupMemberDto {

    private Long accountGroupId;
    private Long accountId;
    private String role;

    private AccountGroup accountGroup;
    private Account account;
}
