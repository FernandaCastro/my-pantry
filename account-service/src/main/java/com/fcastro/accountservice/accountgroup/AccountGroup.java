package com.fcastro.accountservice.accountgroup;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "accountGroup")
@Table(name = "account_group", schema = "account")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_account_group_id")
    private AccountGroup parentAccountGroup;
}
