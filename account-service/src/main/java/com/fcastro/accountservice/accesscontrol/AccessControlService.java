package com.fcastro.accountservice.accesscontrol;

import com.fcastro.accountservice.accountgroup.AccountGroup;
import com.fcastro.accountservice.cache.AccessControlCacheService;
import com.fcastro.accountservice.cache.MemberCacheDto;
import com.fcastro.accountservice.cache.MemberCacheService;
import com.fcastro.accountservice.exception.AccessControlNotDefinedException;
import com.fcastro.accountservice.role.RoleService;
import com.fcastro.app.config.MessageTranslator;
import com.fcastro.security.core.model.AccessControlDto;
import com.fcastro.security.core.model.AccountGroupDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class AccessControlService {
    private final AccessControlRepository accessControlRepository;
    private final AccessControlCacheService accessControlCacheService;
    private final RoleService roleService;
    private final MemberCacheService memberCacheService;

    public AccessControlService(AccessControlRepository accessControlRepository, AccessControlCacheService accessControlCacheService, RoleService roleService, MemberCacheService memberCacheService) {
        this.accessControlRepository = accessControlRepository;
        this.accessControlCacheService = accessControlCacheService;
        this.roleService = roleService;
        this.memberCacheService = memberCacheService;
    }

    public boolean isAuthorized(String clazz, Long clazzId, List<Long> groupIds) {
        var access = accessControlRepository.findAllByGroupIds(clazz, clazzId, groupIds);
        return access != null && access.size() > 0;
    }

    public AccessControlDto get(String clazz, Long clazzId) {
        return accessControlRepository.findByClazzAndClazzId(clazz, clazzId)
                .map(this::convertToDto)
                .orElseThrow(() -> new AccessControlNotDefinedException(MessageTranslator.getMessage("error.access.control.not.defined", clazz, String.valueOf(clazzId))));
    }

    public List<AccessControlDto> getAll(Long accountGroupId) {
        var list = accessControlRepository.findAllByAccountGroupId(accountGroupId);
        return list.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    //Strict related to the AccountGroup informed. Do not consider accountgroup hierarchy
    public List<AccessControlDto> getNonHierarchical(String email, String clazz, Long accountGroupId) {
        if (email.isEmpty()) return List.of();

        List<AccessControlDto> list = new ArrayList<>();

        //Get User's AccountGroups/Roles from MemberCache, otherwise from database
        var groupMemberList = memberCacheService.getFromCache(email);

        //Email + Clazz + AccountGroup
        if (clazz != null && !clazz.isEmpty() && accountGroupId != null) {
            list = getNonHierarchicallyByAccountGroup(clazz, accountGroupId, groupMemberList);
            //list = accessControlRepository.findAllByEmailAndClazzAndAccountGroupIdStrict(email, clazz, accountGroupId);
        }

        return list;
    }

    private void consumeList(Consumer<AccessControlDto> consumer, Set<Long> clazzIds, String clazz, Long groupId, Long parentGroupId) {

        var parentGroup = parentGroupId == null ? null : AccountGroupDto.builder().id(parentGroupId).build();
        var group = AccountGroupDto.builder().id(groupId).parentAccountGroup(parentGroup).build();

        clazzIds.forEach(id -> {
            consumer.accept(AccessControlDto.builder()
                    .clazzId(id)
                    .clazz(clazz)
                    .accountGroup(group)
                    .build());
        });
    }

    //Consider accountgroup hierarchy
    public List<AccessControlDto> getHierarchical(String email, String clazz, Long clazzId, Long accountGroupId, String permission) {
        if (email.isEmpty()) return List.of();

        List<AccessControlDto> list = new ArrayList<>();

        //Get User's AccountGroups/Roles from MemberCache, otherwise from database
        var groupMemberList = memberCacheService.getFromCache(email);

        //Email + Clazz + AccountGroup
        if (clazz != null && !clazz.isEmpty() && accountGroupId != null) {
            list = getHierarchicallyByAccountGroup(clazz, accountGroupId, groupMemberList);

            //Email + Clazz + ClazzId
        } else if (clazz != null && !clazz.isEmpty() && clazzId != null) {
            list = getHierarchicallyByClazzId(clazz, clazzId, groupMemberList);

            //Email + Clazz + Permission
        } else if (clazz != null && !clazz.isEmpty() && permission != null && !permission.isEmpty()) {
            list = getHierarchicallyByClazzAndPermission(clazz, permission, groupMemberList);

            //Email + Clazz
        } else if (clazz != null && !clazz.isEmpty()) {
            list = getHierarchicallyByClazz(clazz, groupMemberList);

        } else {
            throw new AccessControlNotDefinedException("error.access.control.invalid.criteria");
        }

        return list;
    }

    private List<AccessControlDto> getHierarchicallyByClazz(String clazz, List<MemberCacheDto> groupMemberList) {

        var memo = new HashSet<Long>();

        //Filter by clazz, where user is member of groups and its parentGroups
        return groupMemberList.stream()
                .mapMulti((MemberCacheDto member, Consumer<AccessControlDto> consumer) -> {

                    //Get all objects associated to the AccountGroup
                    var objectsInGroup = accessControlCacheService.getFromCache(member.getAccountGroupId(), clazz);
                    consumeList(consumer, objectsInGroup, clazz, member.getAccountGroupId(), member.getParentAccountGroupId());
                    memo.add(member.getAccountGroupId());

                    //Get all associated to the ParentAccountGroup
                    if (member.getParentAccountGroupId() != null && member.getParentAccountGroupId() > 0 && !memo.contains(member.getParentAccountGroupId())) {
                        var objectsInParentGroup = accessControlCacheService.getFromCache(member.getParentAccountGroupId(), clazz);
                        consumeList(consumer, objectsInParentGroup, clazz, member.getParentAccountGroupId(), null);
                        memo.add(member.getParentAccountGroupId());
                    }

                }).collect(Collectors.toList());
    }

    private List<AccessControlDto> getNonHierarchicallyByAccountGroup(String clazz, Long accountGroupId, List<MemberCacheDto> groupMemberList) {
        return groupMemberList.stream()

                //Filter by accountGroupId
                .filter(memberCacheDto -> memberCacheDto.getAccountGroupId() == accountGroupId)

                //return all that the groupId and clazz match
                .mapMulti((MemberCacheDto member, Consumer<AccessControlDto> consumer) -> {

                    //Get all  associated to the AccountGroup
                    var objectsInGroup = accessControlCacheService.getFromCache(member.getAccountGroupId(), clazz);
                    consumeList(consumer, objectsInGroup, clazz, member.getAccountGroupId(), member.getParentAccountGroupId());

                }).collect(Collectors.toList());
    }

    private List<AccessControlDto> getHierarchicallyByAccountGroup(String clazz, Long accountGroupId, List<MemberCacheDto> groupMemberList) {

        //Filter by accountGroupId
        var filteredByGroupId = groupMemberList.stream()
                .filter(memberCacheDto -> memberCacheDto.getAccountGroupId() == accountGroupId)
                .collect(Collectors.toList());

        //Filter By clazz
        return getHierarchicallyByClazz(clazz, filteredByGroupId);
    }

    private List<AccessControlDto> getHierarchicallyByClazzId(String clazz, Long clazzId, List<MemberCacheDto> groupMemberList) {

        //Filter By clazz
        var filteredByClazz = getHierarchicallyByClazz(clazz, groupMemberList);

        //Filter by clazzId
        return filteredByClazz.stream()
                .filter(accessControl -> accessControl.getClazzId() == clazzId)
                .collect(Collectors.toList());
    }

    private List<AccessControlDto> getHierarchicallyByClazzAndPermission(String clazz, String permission, List<MemberCacheDto> groupMemberList) {

        //Filter by Groups where user has the permission
        var filteredByPermission = groupMemberList.stream()

                //Filter to only groups where user has the permission
                .filter(member -> {
                    return roleService.getRole(member.getRoleId()).getPermissions().stream()
                            .anyMatch(p -> p.getId().equalsIgnoreCase(permission));
                })
                .collect(Collectors.toList());

        //Filter By clazz
        return getHierarchicallyByClazz(clazz, filteredByPermission);

    }

    public void save(AccessControlDto accessControlDto) {
        var accessControl = convertToEntity(accessControlDto);
        accessControlRepository.save(accessControl);
        accessControlCacheService.updateCache(accessControl.getAccountGroup().getId(), accessControl.getClazz(), accessControl.getClazzId());
    }

    @Transactional
    public void delete(String clazz, Long clazzId) {
        var found = accessControlRepository.findByClazzAndClazzId(clazz, clazzId);
        if (found.isPresent()) {
            accessControlRepository.deleteAllByClazzAndClazzId(clazz, clazzId);

            var accessControl = found.get();
            accessControlCacheService.deleteFromCache(accessControl.getAccountGroup().getId(), accessControl.getClazz(), accessControl.getClazzId());
        }
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
