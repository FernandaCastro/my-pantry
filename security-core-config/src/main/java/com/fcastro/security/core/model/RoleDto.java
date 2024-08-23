package com.fcastro.security.core.model;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {

    private String id;

    private List<PermissionDto> permissions;
}
