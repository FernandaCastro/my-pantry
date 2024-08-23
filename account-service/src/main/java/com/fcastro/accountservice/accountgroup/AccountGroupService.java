package com.fcastro.accountservice.accountgroup;

import com.fcastro.accountservice.accesscontrol.AccessControlService;
import com.fcastro.accountservice.account.Account;
import com.fcastro.accountservice.accountgroupmember.AccountGroupMemberRole;
import com.fcastro.accountservice.accountgroupmember.AccountGroupMemberService;
import com.fcastro.accountservice.exception.NotAllowedException;
import com.fcastro.app.config.MessageTranslator;
import com.fcastro.app.exception.ResourceNotFoundException;
import com.fcastro.security.core.model.AccountGroupDto;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccountGroupService {

    private final AccountGroupRepository repository;
    private final AccountGroupMemberService groupMemberService;
    private final AccessControlService accessControlService;

    public AccountGroupService(AccountGroupRepository repository, AccountGroupMemberService groupMemberService, AccessControlService accessControlService) {
        this.repository = repository;
        this.groupMemberService = groupMemberService;
        this.accessControlService = accessControlService;
    }

    public Optional<AccountGroupDto> get(long id) {
        return repository.findById(id)
                .map(this::convertToDTO);
    }

    public List<AccountGroupDto> getAll() {
        List<AccountGroup> listEntity = repository.findAllStrict(SecurityContextHolder.getContext().getAuthentication().getName());
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    //TODO: Pageable
    public List<AccountGroupDto> getAll(String email) {
        var listEntity = repository.findAllStrict(email);
        return listEntity.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public AccountGroupDto createParentGroup(Account account) {
        var accountGroup = AccountGroup.builder()
                .name("Group " + account.getName())
                .build();

        accountGroup = repository.save(accountGroup);
        groupMemberService.createParentGroupMember(account.getId(), accountGroup.getId());

        return convertToDTO(accountGroup);
    }

    public AccountGroupDto save(AccountGroupDto dto) {

        //update
        if (dto.getId() != null && repository.findById(dto.getId()).isPresent()) {

            var member = groupMemberService.getByGroupIdAndEmail(dto.getId(), SecurityContextHolder.getContext().getAuthentication().getName()).get();
            if (member == null || !AccountGroupMemberRole.OWNER.value.equals(member.getRole().getId())) {
                throw new NotAllowedException(MessageTranslator.getMessage("error.update.group.not.allowed"));
            }

            var accountGroup = repository.save(convertToEntity(dto));
            return convertToDTO(accountGroup);
        }

        //new child group, so find  and associate the parent
        var parentGroup = repository.findParentAccountGroup(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.parent.group.not.found")));

        var accountGroup = convertToEntity(dto);
        accountGroup.setParentAccountGroup(parentGroup);
        accountGroup = repository.save(accountGroup);

        groupMemberService.createChildGroupMember(SecurityContextHolder.getContext().getAuthentication().getName(), accountGroup.getId());

        return convertToDTO(accountGroup);
    }

    public void delete(long accountGroupId) {
        var accountGroup = repository.findById(accountGroupId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageTranslator.getMessage("error.group.not.found")));

        var member = groupMemberService.getByGroupIdAndEmail(accountGroupId, SecurityContextHolder.getContext().getAuthentication().getName()).get();
        if (member == null || !AccountGroupMemberRole.OWNER.value.equals(member.getRole().getId())) {
            throw new NotAllowedException(MessageTranslator.getMessage("error.delete.group.not.allowed"));
        }


        if (accountGroup.getParentAccountGroup() == null)
            throw new NotAllowedException(MessageTranslator.getMessage("error.delete.main.group.not.allowed"));

        var inUse = accessControlService.getAll(accountGroupId);
        if (inUse.size() > 0)
            throw new NotAllowedException(MessageTranslator.getMessage("error.delete.not.empty.group.not.allowed"));

        repository.deleteById(accountGroupId);
    }

    private AccountGroupDto convertToDTO(AccountGroup entity) {
        if (entity == null) return null;

        AccountGroupDto parentAccountGroup = null;
        if (entity.getParentAccountGroup() != null) {
            parentAccountGroup = AccountGroupDto.builder()
                    .id(entity.getParentAccountGroup().getId())
                    .name(entity.getParentAccountGroup().getName())
                    .build();
        }

        return AccountGroupDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .parentAccountGroup(parentAccountGroup)
                .build();
    }

    private AccountGroup convertToEntity(AccountGroupDto dto) {
        if (dto == null) return null;

        AccountGroup parentAccountGroup = null;
        if (dto.getParentAccountGroup() != null) {
            parentAccountGroup = AccountGroup.builder()
                    .id(dto.getParentAccountGroup().getId())
                    .build();
        }
        return AccountGroup.builder()
                .id(dto.getId())
                .name(dto.getName())
                .parentAccountGroup(parentAccountGroup)
                .build();
    }

}
