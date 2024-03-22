package com.fcastro.accountService.role;

import com.fcastro.accountService.permission.Permission;
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
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(
            name = "role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private List<Permission> permissions;
}
