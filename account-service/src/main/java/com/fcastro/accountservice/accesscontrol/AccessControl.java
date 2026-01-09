package com.fcastro.accountservice.accesscontrol;

import com.fcastro.accountservice.accountgroup.AccountGroup;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "accessControl")
@Table(name = "access_control", schema = "account")
@IdClass(AccessControlKey.class)
public class AccessControl {

    @Id
    @Column(nullable = false)
    private String clazz;

    @Id
    @Column(nullable = false)
    private Long clazzId;

    @OneToOne
    @JoinColumn(name = "account_group_id", nullable = false)
    private AccountGroup accountGroup;
}
