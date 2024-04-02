package com.fcastro.accountservice.accesscontrol;

import com.fcastro.accountservice.accountgroup.AccountGroup;
import com.fcastro.accountservice.exception.AccessControlNotDefinedException;
import com.fcastro.security.core.model.AccessControlDto;
import com.fcastro.security.core.model.AccountGroupDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccessControlService {
    private final AccessControlRepository accessControlRepository;

    public AccessControlService(AccessControlRepository accessControlRepository) {
        this.accessControlRepository = accessControlRepository;
    }

    public boolean isAuthorized(String clazz, Long clazzId, List<Long> groupIds) {
        var access = accessControlRepository.findAllByGroupIds(clazz, clazzId, groupIds);
        return access != null && access.size() > 0;
    }

    public AccessControlDto get(String clazz, Long clazzId) {
        return accessControlRepository.findByClazzAndClazzId(clazz, clazzId)
                .map(this::convertToDto)
                .orElseThrow(() -> new AccessControlNotDefinedException("No access control defined for " + clazz + "[" + clazzId + "]"));
    }

    public List<AccessControlDto> getAll(Long accountGroupId) {
        var list = accessControlRepository.findAllByAccountGroupId(accountGroupId);
        return list.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public void save(AccessControlDto accessControlDto) {
        var accessControl = convertToEntity(accessControlDto);
        accessControlRepository.save(accessControl);
    }

    public void delete(String clazz, Long clazzId) {
        accessControlRepository.deleteAllByClazzAndClazzId(clazz, clazzId);
    }

    private AccessControlDto convertToDto(AccessControl entity) {
        if (entity == null) return null;
        return AccessControlDto.builder()
                .clazz(entity.getClazz())
                .clazzId(entity.getClazzId())
                .accountGroup(AccountGroupDto.builder()
                        .id(entity.getAccountGroup()
                                .getId()).name(entity.getAccountGroup().getName())
                        .build())
                .build();
    }


    private AccessControl convertToEntity(AccessControlDto dto) {
        if (dto == null) return null;
        return AccessControl.builder()
                .clazz(dto.getClazz())
                .clazzId(dto.getClazzId())
                .accountGroup(AccountGroup.builder()
                        .id(dto.getAccountGroup().getId())
                        .build())
                .build();
    }
}
