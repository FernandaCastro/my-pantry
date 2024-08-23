package com.fcastro.accountservice.permission;

import com.fcastro.accountservice.role.Role;
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
    private String id;

    @ManyToMany(mappedBy = "permissions")
    private List<Role> roles;
}
