package com.fcastro.accountservice.role;

import com.fcastro.accountservice.permission.PermissionDto;
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
