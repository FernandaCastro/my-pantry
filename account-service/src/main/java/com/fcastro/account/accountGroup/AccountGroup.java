package com.fcastro.account.accountGroup;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "accountGroup")
@Table(name = "account_group")
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
}
