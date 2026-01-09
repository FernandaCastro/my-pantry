package com.fcastro.accountservice.role;

import com.fcastro.accountservice.permission.Permission;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity(name = "role")
@Table(name = "ROLE", schema = "account")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role implements Serializable {

    @Serial
    private static final long serialVersionUID = 529714589336904406L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @ManyToMany
    @JoinTable(
            name = "role_permission", schema = "account",
            joinColumns = @JoinColumn(name = "role_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "permission_id", nullable = false))
    private List<Permission> permissions;
}
