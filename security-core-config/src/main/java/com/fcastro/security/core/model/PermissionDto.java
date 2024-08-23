package com.fcastro.security.core.model;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDto {

    private String id;

    private RoleDto roleDto;
}
