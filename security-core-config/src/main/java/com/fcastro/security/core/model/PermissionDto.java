package com.fcastro.security.core.model;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDto {

    private Long id;

    private String name;

    private RoleDto roleDto;
}
