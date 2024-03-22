package com.fcastro.security.accessControl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Optional<AccessControl> findAllByClazzAndClazzId(String clazz, Long clazzId) {
        return accessControlRepository.findAllByClazzAndClazzId(clazz, clazzId);
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
}
