package com.fcastro.account.accountGroupMember;

import com.fcastro.account.account.Account;
import com.fcastro.account.accountGroup.AccountGroup;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "accountGroupAccount")
@Table(name = "account_group_account")
@IdClass(AccountGroupMemberKey.class)
public class AccountGroupMember {

    @Id
    private Long accountGroupId;

    @Id
    private Long accountId;

    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("accountGroupId")
    @JoinColumn(name = "account_group_id")
    private AccountGroup accountGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("accountId")
    @JoinColumn(name = "account_id")
    private Account account;
}
