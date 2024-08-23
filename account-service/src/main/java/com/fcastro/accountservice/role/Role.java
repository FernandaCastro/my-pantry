package com.fcastro.accountservice.role;

import com.fcastro.accountservice.permission.Permission;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity(name = "role")
@Table(name = "ROLE")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @ManyToMany
    @JoinTable(
            name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "permission_id", nullable = false))
    private List<Permission> permissions;
}
