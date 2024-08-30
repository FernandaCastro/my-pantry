package com.fcastro.accountservice.role;

import com.fcastro.accountservice.config.CustomCacheConfig;
import com.fcastro.accountservice.permission.Permission;
import com.fcastro.app.config.MessageTranslator;
import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.security.core.model.PermissionDto;
import com.fcastro.security.core.model.RoleDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<RoleDto> getAll() {
        //var list = roleRepository.findAll(Sort.by("name"));
        var list = roleRepository.findAllOrderByAccessLevel();
        return list.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Cacheable(value = CustomCacheConfig.ROLE_CACHE, key = "#id")
    public RoleDto getRole(String id) {
        return roleRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.role.not.found")));
    }

    private RoleDto convertToDto(Role entity) {
        var permissions = entity.getPermissions();

        return RoleDto.builder()
                .id(entity.getId())
                .permissions(convertToDtoList(entity.getPermissions()))
                .build();
    }

    private List<PermissionDto> convertToDtoList(List<Permission> entities) {
        if (entities == null) return null;
        var list = new ArrayList<PermissionDto>();
        entities.forEach((e) ->
                list.add(PermissionDto.builder()
                        .id(e.getId())
                        .build()));
        list.sort(Comparator.comparing(PermissionDto::getId));
        return list;
    }
}
