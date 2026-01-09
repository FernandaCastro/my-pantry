package com.fcastro.accountservice.accountgroupmember;

import com.fcastro.accountservice.account.Account;
import com.fcastro.accountservice.accountgroup.AccountGroup;
import com.fcastro.accountservice.role.Role;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "accountGroupMember")
@Table(name = "account_group_member", schema = "account")
@IdClass(AccountGroupMemberKey.class)
public class AccountGroupMember {

    @Id
    private Long accountGroupId;

    @Id
    private Long accountId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("accountGroupId")
    @JoinColumn(name = "account_group_id")
    private AccountGroup accountGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("accountId")
    @JoinColumn(name = "account_id")
    private Account account;
}
