package com.fcastro.accountservice.accesscontrol;

import com.fcastro.accountservice.accountgroup.AccountGroup;
import com.fcastro.accountservice.exception.AccessControlNotDefinedException;
import com.fcastro.security.core.model.AccessControlDto;
import com.fcastro.security.core.model.AccountGroupDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    //Authorization method
    public List<AccessControlDto> getAllByEmailAndAccessControl(String email, String clazz, Long clazzId, Long accountGroupId, String permission) {
        if (email.isEmpty()) return List.of();

        List<AccessControl> list = null;

        //Email + Clazz + AccountGroup
        if (clazz != null && !clazz.isEmpty() && accountGroupId != null) {
            list = accessControlRepository.findAllByEmailAndClazzAndAccountGroupId(email, clazz, accountGroupId);

            //Email + Clazz + ClazzId
        } else if (clazz != null && !clazz.isEmpty() && clazzId != null) {
            list = accessControlRepository.findAllByEmailAndClazzAndClazzId(email, clazz, clazzId);

            //Email + Clazz + Permission
        } else if (clazz != null && !clazz.isEmpty() && permission != null && !permission.isEmpty()) {
            list = accessControlRepository.findAllByEmailAndClazzAndPermission(email, clazz, permission);

            //Email + Clazz
        } else if (clazz != null && !clazz.isEmpty()) {
            list = accessControlRepository.findAllByEmailAndClazz(email, clazz);

        } else {
            throw new AccessControlNotDefinedException("Unable to retrieve Access Control. The criteria informed is invalid.");
        }

        return list.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public void save(AccessControlDto accessControlDto) {
        var accessControl = convertToEntity(accessControlDto);
        accessControlRepository.save(accessControl);
    }

    @Transactional
    public void delete(String clazz, Long clazzId) {
        accessControlRepository.deleteAllByClazzAndClazzId(clazz, clazzId);
    }

    //Authorization method
    public AccessControlDto hasPermissionInObject(String email, String permission, String clazz, Long clazzId) {
        var accessControl = convertToDto(accessControlRepository.hasPermissionInObject(email, permission, clazz, clazzId));
        return accessControl;
    }

    //Authorization method
    public List<AccessControlDto> hasPermissionInObjectList(String email, String permission, String clazz, List<Long> clazzIds) {
        var list = accessControlRepository.hasPermissionInObjectList(email, permission, clazz, clazzIds);
        return list.stream().map(this::convertToDto).toList();
    }


    private AccessControlDto convertToDto(AccessControl entity) {
        if (entity == null) return null;

        AccountGroupDto parentAccountGroup = null;
        if (entity.getAccountGroup().getParentAccountGroup() != null) {
            parentAccountGroup = AccountGroupDto.builder()
                    .id(entity.getAccountGroup().getParentAccountGroup().getId())
                    .name(entity.getAccountGroup().getParentAccountGroup().getName())
                    .build();
        }
        return AccessControlDto.builder()
                .clazz(entity.getClazz())
                .clazzId(entity.getClazzId())
                .accountGroup(AccountGroupDto.builder()
                        .id(entity.getAccountGroup().getId())
                        .name(entity.getAccountGroup().getName())
                        .parentAccountGroup(parentAccountGroup)
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
