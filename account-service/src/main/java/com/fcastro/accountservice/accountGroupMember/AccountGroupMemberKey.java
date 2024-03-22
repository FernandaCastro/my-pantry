package com.fcastro.accountservice.accountGroupMember;

import jakarta.persistence.Column;
import lombok.*;

import java.io.Serializable;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AccountGroupMemberKey implements Serializable {

    @Column(name = "account_group_id")
    private long accountGroupId;

    @Column(name = "account_id")
    private long accountId;
}
