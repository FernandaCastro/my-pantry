package com.fcastro.security.accesscontrol;

import com.fcastro.security.exception.AccessControlNotDefinedException;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void save(String clazz, Long clazzId, Long accountGroupId) {
        var accessControl = AccessControl.builder()
                .clazz(clazz)
                .clazzId(clazzId)
                .accountGroupId(accountGroupId)
                .build();
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
                .accountGroupId(entity.getAccountGroupId())
                .build();
    }
}
