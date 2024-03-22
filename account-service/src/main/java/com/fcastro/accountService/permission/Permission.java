package com.fcastro.accountService.permission;

import com.fcastro.accountService.role.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity(name = "permission")
@Table(name = "PERMISSION")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "permissions")
    private List<Role> roles;
}
