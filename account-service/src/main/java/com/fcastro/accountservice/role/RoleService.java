package com.fcastro.accountservice.role;

import com.fcastro.accountservice.permission.Permission;
import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.security.core.model.PermissionDto;
import com.fcastro.security.core.model.RoleDto;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<RoleDto> getAll() {
        var list = roleRepository.findAll(Sort.by("name"));
        return list.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public RoleDto getRole(Long id) {
        return roleRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found."));
    }

    public RoleDto getRole(String name) {
        return roleRepository.findByName(name)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found."));
    }

    private RoleDto convertToDto(Role entity) {
        var permissions = entity.getPermissions();

        return RoleDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .permissions(convertToDtoList(entity.getPermissions()))
                .build();
    }

    private List<PermissionDto> convertToDtoList(List<Permission> entities) {
        if (entities == null) return null;
        var list = new ArrayList<PermissionDto>();
        entities.forEach((e) ->
                list.add(PermissionDto.builder()
                        .id(e.getId())
                        .name(e.getName())
                        .build()));
        return list;
    }
}
